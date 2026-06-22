package komple.gradle.tool.task

import komple.exec.KompleExecTask
import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.gradle.util.dashCased
import komple.platform.Host
import komple.task.TaskContext
import komple.task.doFirstWhenInputsChanged
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import komple.tool.task.ExecToolTaskContext
import komple.tool.task.TaskRegistrationScope
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
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

        if (outputs.files.isEmpty) {
            logger.warn("Task $name did not registered an output directory, using context one")
            outputs.dir(outputDirectory)
        }
    }

    /**
     * Invokes [configurator] and ensures no output file(s) were registered for task.
     */
    protected inline fun <T : Task, C : ExecToolTaskContext> T.configureTask(
        context: C,
        configurator: T.(C) -> Unit,
        deleteFirst: Boolean
    ) {
        // Convenience for task who aims to execute commands
        if (this is KompleExecTask) {
            execEnvironment = context.execEnvironment
        }

        configureTask(
            context = context,
            outputDirectory = project.provider(context::outputDirectory),
            configurator = configurator
        )

        if (deleteFirst) {
            val fileOperations = project.serviceOf<FileSystemOperations>()

            context.doFirstWhenInputsChanged {
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
        exception: Exception,
    ): TaskProvider<*> = context.project.tasks.registerToolTask(
        name = toolTaskName(postfix),
        type = DefaultTask::class,
        cacheable = false
    ) {
        group = null

        doLast {
            throw exception
        }
    }

    /**
     * Registers a task that always fails when executed with [UnsupportedHostException].
     */
    protected fun registerUnsupportedTask(postfix: String): TaskProvider<*> =
        registerFailureTask(postfix, UnsupportedHostException("Host is not supported"))
}