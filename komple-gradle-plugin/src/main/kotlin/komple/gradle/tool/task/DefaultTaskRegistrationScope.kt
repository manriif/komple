package komple.gradle.tool.task

import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.gradle.util.dashCased
import komple.platform.Host
import komple.task.TaskContext
import komple.task.doFirstWhenOutputChanged
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import komple.tool.task.TaskRegistrationScope
import komple.tool.task.ToolTaskContext
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import kotlin.reflect.KClass

/**
 * Default base implementation for [TaskRegistrationScope].
 */
internal abstract class DefaultTaskRegistrationScope<Extension : KompleToolExtension>(
    protected val context: KompleToolConfigContext<Extension>
) : TaskRegistrationScope<Extension>,
    HasExtension<Extension> by context,
    ClosableScope() {

    override val host: Host
        get() = notClosed { CurrentHost }

    override val providers: ProviderFactory
        get() = notClosed { context.project.providers }

    override val toolName: String
        get() = context.toolName

    override val toolNameCompat: String
        get() = toolName.dashCased()

    /**
     * Returns a conventional name for a task and for the tool.
     */
    protected fun toolTaskName(postfix: String): String {
        return toolTaskName(toolName, postfix)
    }

    /**
     * Registers a task [T] and returns a provider to the registered task.
     */
    protected fun <T : Task> registerTask(
        postfix: String,
        type: KClass<T>,
        cacheable: Boolean,
        configure: T.(context: TaskContext) -> Unit
    ): TaskProvider<T> = context.project.tasks.registerToolTask(
        name = toolTaskName(postfix),
        type = type,
        cacheable = cacheable,
        configure
    )

    /**
     * Invokes [configurator] and ensures no output file(s) were registered for task.
     */
    protected inline fun <T : Task, C> T.configureTask(
        context: C,
        outputDirectory: Provider<Directory>,
        configurator: T.(C) -> Unit
    ) {
        configurator(this, context)

        check(outputs.files.isEmpty) {
            "Task must not register output file(s)"
        }

        outputs.dir(outputDirectory)
    }

    /**
     * Invokes [configurator] and ensures no output file(s) were registered for task.
     */
    protected inline fun <T : Task, C : ToolTaskContext> T.configureTask(
        context: C,
        configurator: T.(C) -> Unit,
        deleteFirst: Boolean
    ) {
        configureTask(
            context = context,
            outputDirectory = project.provider(context::outputDirectory),
            configurator = configurator
        )

        if (deleteFirst) {
            val fileOperations = project.serviceOf<FileSystemOperations>()

            context.doFirstWhenOutputChanged {
                fileOperations.delete {
                    delete(context.outputDirectory)
                }
            }
        }
    }

    /**
     * Registers a task that always fails when executed.
     */
    protected fun registerFailureTask(
        postfix: String,
        raiseError: () -> Nothing
    ): TaskProvider<*> {
        return context.project.tasks.registerToolTask(
            name = toolTaskName(postfix),
            type = DefaultTask::class,
            cacheable = false
        ) {
            group = null

            doLast {
                raiseError()
            }
        }
    }

    /**
     * Registers a task that always fails when executed with [UnsupportedHostException].
     */
    protected fun registerUnsupportedTask(postfix: String): TaskProvider<*> {
        return registerFailureTask(postfix) {
            throw UnsupportedHostException("Host is not supported")
        }
    }
}