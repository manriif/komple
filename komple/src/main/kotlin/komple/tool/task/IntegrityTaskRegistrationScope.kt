package komple.tool.task

import de.undercouch.gradle.tasks.download.VerifyAction
import komple.tool.extension.KompleToolExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Scope for integrity task registration.
 */
public interface IntegrityTaskRegistrationScope<Extension : KompleToolExtension> :
    TaskRegistrationScope<Extension> {

    /**
     * Registers a task of type [T], [configure]s it and returns it.
     * 
     * Downloaded file(s) lives in the [TaskDirectory] passed to [configure].
     * The task should only perform integrity checking against downloaded file(s).
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(directory: TaskDirectory) -> Unit
    ): TaskProvider<T>

    /**
     * Returns a task that skips integrity checking, causing downloaded files to be considered safe.
     *
     * Note that skipping integrity check is discouraged if the tools is getting downloaded from an
     * external source.
     */
    override fun skip(): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T], [configure]s it and returns it.
 *
 * Downloaded file(s) lives in the [TaskDirectory] passed to [configure].
 * The task should only perform integrity checking against downloaded file(s).
 */
public inline fun <reified T : Task> IntegrityTaskRegistrationScope<*>.register(
    noinline configure: T.(directory: TaskDirectory) -> Unit
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
public fun IntegrityTaskRegistrationScope<*>.checksum(
    checksum: Provider<String>,
    algorithm: Algorithm
): TaskProvider<*> = register<DefaultTask> { directory ->
    val verify = VerifyAction(project.layout).apply {
        algorithm(algorithm.toMessageDigestConstant())
    }

    doLast {
        verify.apply {
            src(directory.singleFile)
            checksum(checksum.get())
        }

        verify.execute()
    }
}

/**
 * Registers a default integrity task that validate the file integrity against [checksum] using
 * [algorithm].
 *
 * It is assumed that only a single file has been downloaded.
 */
public fun IntegrityTaskRegistrationScope<*>.checksum(
    checksum: String,
    algorithm: Algorithm
): TaskProvider<*> = checksum(
    checksum = providers.provider { checksum },
    algorithm = algorithm
)