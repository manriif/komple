package komple.tool.task

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
    TaskRegistrationScope<Extension> {

    /**
     * Registers a task of type [T], [configure]s it and returns that registered task.
     *
     * The directory where extracted file(s) lives and where installed file(s) must be written to
     * can be obtained from the [InstallTaskContext] passed to [configure].
     *
     * The task must not register any output and instead use the context directory to put all
     * its files.
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
 * Registers a task of type [T] subclass of [InstallTask] and [configure]s it.
 *
 * The [InstallTask.context] is configured before [configure] is invoked.
 */
public inline fun <reified T : InstallTask> InstallTaskRegistrationScope<*>.install(
    cacheable: Boolean = false,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = register<T>(cacheable) { context ->
    this.context = context
    configure?.invoke(this)
}

/**
 * Registers a task of type [T] subclass of [CommandInstallTask] and [configure]s it.
 *
 * The [CommandInstallTask.context] property is configured before [configure] is invoked.
 */
public inline fun <reified T : CommandInstallTask> InstallTaskRegistrationScope<*>.command(
    cacheable: Boolean = false,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = install<T>(cacheable, configure)

/**
 * Registers a task configuring tool files using a command obtained via [provider].
 *
 * Note that the returned task is cacheable.
 */
public fun InstallTaskRegistrationScope<*>.command(
    provider: InstallCommandProvider
): TaskProvider<DefaultCommandInstallTask> = command(false) {
    this.provider = provider
}