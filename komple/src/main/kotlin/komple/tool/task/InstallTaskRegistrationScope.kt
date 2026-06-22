package komple.tool.task

import komple.task.enableTracking
import komple.task.install.CommandInstallTask
import komple.task.install.DefaultCommandInstallTask
import komple.task.install.InstallCommandProvider
import komple.task.install.InstallTask
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Scope for install task registration.
 */
public interface InstallTaskRegistrationScope<Extension : KompleToolExtension> :
    TaskRegistrationScope<Extension, InstallTaskContext> {

    /**
     * Registers a task of type [T], [configure]s it and returns it.
     *
     * The directory where extracted (or downloaded, if extraction was skipped) file(s) lives and
     * where installed file(s) must be written to can be obtained from the
     * [InstallTaskContext.inputDirectory] and [InstallTaskContext.outputDirectory] passed to
     * [configure]. The input directory should eventually be registered as task's input. The output
     * directory must be registered as the task's only output directory.
     */
    public override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(context: InstallTaskContext) -> Unit
    ): TaskProvider<T>

    /**
     * Registers a task that skips installation, causing extracted (or downloaded, if extraction was
     * skipped) file(s) to be used as installed files.
     */
    override fun skip(): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T], [configure]s it and returns it.
 *
 * The directory where extracted (or downloaded, if extraction was skipped) file(s) lives and
 * where installed file(s) must be written to can be obtained from the
 * [InstallTaskContext.inputDirectory] and [InstallTaskContext.outputDirectory] passed to
 * [configure]. The input directory should eventually be registered as task's input. The output
 * directory must be registered as the task's only output directory.
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
 * Registers a task of type [T] subclass of [InstallTask] and [configure]s it.
 *
 * The [InstallTask.execEnvironment], [InstallTask.tracker], [InstallTask.outputDirectory] and
 * [InstallTask.inputDirectory] properties are configured before [configure] is invoked.
 *
 * Tracking is enabled by default.
 */
public inline fun <reified T : InstallTask> InstallTaskRegistrationScope<*>.install(
    cacheable: Boolean = false,
    noinline configure: (T.(context: InstallTaskContext) -> Unit)? = null
): TaskProvider<T> = outputTool(cacheable) { context ->
    this.inputDirectory = context.inputDirectory
    context.tracker.enableTracking()
    configure?.invoke(this, context)
}

/**
 * Registers a task of type [T] subclass of [CommandInstallTask] and [configure]s it.
 *
 * The [CommandInstallTask.execEnvironment], [CommandInstallTask.tracker],
 * [CommandInstallTask.outputDirectory] and [CommandInstallTask.inputDirectory] properties are
 * configured before [configure] is invoked.
 *
 * Tracking is enabled by default.
 */
public inline fun <reified T : CommandInstallTask> InstallTaskRegistrationScope<*>.command(
    cacheable: Boolean = false,
    noinline configure: (T.(context: InstallTaskContext) -> Unit)? = null
): TaskProvider<T> = install<T>(cacheable, configure)

/**
 * Registers a task configuring tool files using a command obtained via [provider].
 *
 * Tracking is enabled by default.
 * Note that the returned task is cacheable.
 */
public fun InstallTaskRegistrationScope<*>.command(
    provider: InstallCommandProvider
): TaskProvider<DefaultCommandInstallTask> = command(false) {
    this.provider = provider
}