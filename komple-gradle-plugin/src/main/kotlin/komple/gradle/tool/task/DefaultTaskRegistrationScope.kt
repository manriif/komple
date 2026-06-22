package komple.gradle.tool.task

import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.gradle.util.dashCased
import komple.platform.Host
import komple.task.TaskStateTracker
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import komple.tool.task.IntegrityTaskContext
import komple.tool.task.TaskRegistrationScope
import komple.tool.task.ToolTaskContext
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default base implementation for [TaskRegistrationScope].
 */
internal abstract class DefaultTaskRegistrationScope<E : KompleToolExtension, C : ToolTaskContext>(
    protected val context: KompleToolConfigContext<E>
) : TaskRegistrationScope<E, C>,
    HasExtension<E> by context,
    ClosableScope() {

    protected abstract val taskPostfix: String

    override val host: Host
        get() = notClosed { CurrentHost }

    override val providers: ProviderFactory
        get() = notClosed { context.project.providers }

    override val toolName: String
        get() = context.toolName

    override val toolNameCompat: String
        get() = toolName.dashCased()

    protected val toolTaskName: String
        get() = toolTaskName(toolName, taskPostfix)

    /**
     * Registers a task [T] as [toolTaskName].
     */
    protected fun <T : Task> registerToolTask(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(tracker: TaskStateTracker) -> Unit
    ): TaskProvider<T> = context.project.tasks.registerToolTask(
        name = toolTaskName,
        type = klass,
        cacheable = cacheable,
        configure = configure
    )

    /**
     * Registers a task that always fails when executed.
     */
    protected fun registerFailureTask(exception: Exception): TaskProvider<*> {
        return context.project.tasks.registerToolTask(
            name = toolTaskName,
            type = DefaultTask::class,
            cacheable = false
        ) {
            group = null

            doLast {
                throw exception
            }
        }
    }

    final override fun unsupported(): TaskProvider<*> =
        registerFailureTask(UnsupportedHostException("Host is not supported"))
}