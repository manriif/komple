package komple.tool.task

import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
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
    public fun skipInstallation(): TaskProvider<*>
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