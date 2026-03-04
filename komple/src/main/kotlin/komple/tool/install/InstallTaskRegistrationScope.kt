package komple.tool.install

import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Scope for install task registration.
 */
public interface InstallTaskRegistrationScope : TaskRegistrationScope {

    /**
     * Registers a task of type [T], [configure]s it and returns that registered task.
     * The task must output a single directory where all the installed files are written to.
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: InstallTaskContext) -> Unit
    ): TaskProvider<T>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T] and [configure]s it.
 * The task must output a single directory where all the installed files are written to.
 */
public inline fun <reified T : Task> InstallTaskRegistrationScope.register(
    noinline configure: T.(context: InstallTaskContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    configure = configure
)