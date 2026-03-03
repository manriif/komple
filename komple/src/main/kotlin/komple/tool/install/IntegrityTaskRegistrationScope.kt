package komple.tool.install

import de.undercouch.gradle.tasks.download.Verify
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Scope for integrity task registration.
 */
public interface IntegrityTaskRegistrationScope : TaskRegistrationScope {

    /**
     * Registers a task of type [T], [configure]s it and returns it.
     * The task should only perform integrity checking against downloaded file(s).
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(inputs: Inputs) -> Unit
    ): TaskProvider<T>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T], [configure]s it and returns it.
 * The task should only perform integrity checking against downloaded file(s).
 */
public inline fun <reified T : Task> IntegrityTaskRegistrationScope.register(
    noinline configure: T.(inputs: Inputs) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    configure = configure
)

/**
 * Registers a default integrity task that validate the file integrity against [checksum] using
 * [algorithm].
 *
 * It is assumed that only a single file has been downloaded.
 */
public fun IntegrityTaskRegistrationScope.checksum(
    checksum: String,
    algorithm: Algorithm
): TaskProvider<*> = register<Verify> { download ->
    src(download.file)
    checksum(checksum)
    algorithm(algorithm.toMessageDigestConstant())
}