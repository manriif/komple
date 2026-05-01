package komple.tool.task

import komple.exec.Command
import komple.exec.createCommandExecutor
import komple.task.doLastWhenOutputChanged
import komple.tool.extension.KompleToolExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.process.ExecOperations
import kotlin.reflect.KClass

/**
 * Scope for install task registration.
 */
public interface InstallTaskRegistrationScope<Extension : KompleToolExtension> :
    TaskRegistrationScope<Extension> {

    /**
     * Registers a task of type [T], [configure]s it and returns that registered task.
     *
     * The directory where extracted file(s) lives and where installed file(s) must be written to
     * can be obtained from the [InstallTaskContext] passed to [configure].
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean = false,
        configure: T.(context: InstallTaskContext) -> Unit
    ): TaskProvider<T>

    /**
     * Registers a task that skips installation, causing extracted files to be used as installed
     * files.
     */
    override fun skip(): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T] and [configure]s it.
 *
 * The directory where extracted file(s) lives and where installed file(s) must be written to
 * can be obtained from the [InstallTaskContext] passed to [configure].
 */
public inline fun <reified T : Task> InstallTaskRegistrationScope<*>.register(
    cacheable: Boolean = false,
    noinline configure: T.(context: InstallTaskContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    cacheable = cacheable,
    configure = configure
)

/**
 * Registers a task that execute a command supplied by [buildCommand] and returns that registered
 * task.
 *
 * The extracted content is first copied from extracted to install directory and the working
 * directory is set to the tool install directory.
 *
 * Note that the task is [cacheable] by default.
 */
public fun InstallTaskRegistrationScope<*>.command(
    cacheable: Boolean = true,
    buildCommand: InstallTaskContext.() -> Command
): TaskProvider<*> = register<DefaultTask>(cacheable) { context ->
    val fileOperations = project.serviceOf<FileSystemOperations>()
    val execOperations = project.serviceOf<ExecOperations>()

    context.doLastWhenOutputChanged {
        fileOperations.copy {
            from(context.extractDirectory.directory)
            into(context.outputDirectory)
        }

        context.execEnvironment.createCommandExecutor(execOperations).execute(
            command = buildCommand(context),
            workingDirectory = context.outputDirectory.asFile
        )
    }
}