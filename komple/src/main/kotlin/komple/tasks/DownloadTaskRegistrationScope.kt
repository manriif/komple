package komple.tasks

import komple.integrity.IntegrityCheckScope
import komple.integrity.IntegrityChecker
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Scope for download task registration.
 */
public interface DownloadTaskRegistrationScope : TaskRegistrationScope {

    /**
     * Registers a task of type [T] and [configure]s it.
     *
     * The task output is returned, and it must resolve to a single regular file.
     * It is the responsibility of the task to verify the downloaded file integrity.
     *
     * The [Directory] passed to [configure] should preferably be used to put download file.
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(outputDirectory: Directory) -> Unit
    ): TaskProvider<T>

    /**
     * Registers a default download task downloading a single file at [url].
     */
    public fun registerDefault(
        url: Provider<String>,
        verify: IntegrityCheckScope.() -> IntegrityChecker
    ): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T] and [configure]s it.
 *
 * The task output is returned, and it must resolve to a single regular file.
 * It is the responsibility of the task to verify the downloaded file integrity.
 *
 * The [Directory] passed to [configure] should preferably be used to put download file.
 */
public inline fun <reified T : Task> DownloadTaskRegistrationScope.register(
    noinline configure: T.(outputDirectory: Directory) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    configure = configure
)