package komple.tasks

import komple.integrity.IntegrityCheck
import komple.integrity.IntegrityCheckScope
import komple.platform.HasHost
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import kotlin.reflect.KClass

/**
 * Scope for download task registration.
 */
public interface DownloadTaskRegistrationScope : HasHost {

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
    ): Provider<RegularFile>

    /**
     * Registers a default download task downloading a single file at [url].
     */
    public fun registerDefault(
        url: Provider<String>,
        verify: IntegrityCheckScope.() -> IntegrityCheck
    ): Provider<RegularFile>
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
): Provider<RegularFile> = register(
    klass = T::class,
    configure = configure
)