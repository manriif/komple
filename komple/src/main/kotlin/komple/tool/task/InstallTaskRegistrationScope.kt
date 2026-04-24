package komple.tool.task

import komple.exec.Command
import komple.tool.extension.KompleToolExtension
import komple.tool.task.register
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.support.serviceOf
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
    noinline configure: T.(context: InstallTaskContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    configure = configure
)

/**
 * Registers a task that execute a command supplied by [buildCommand] and returns that registered
 * task.
 *
 * The extracted content is first copied from extracted to install directory and the working
 * directory is set to the tool install directory.
 */
public fun InstallTaskRegistrationScope<*>.command(
    buildCommand: InstallTaskContext.() -> Command
): TaskProvider<*> = register<DefaultTask> { context ->
    val fileOperations = project.serviceOf<FileSystemOperations>()

    context.doLastWhenOutputChanged {
        fileOperations.copy {
            from(context.extractDirectory.directory)
            into(context.outputDirectory)
        }

        context.execServiceProvider.get().execute(
            command = buildCommand(context),
            workingDirectory = context.outputDirectory.asFile
        )
    }
}