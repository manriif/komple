package komple.tool.task

import komple.task.integrity.ChecksumIntegrityTask
import komple.task.integrity.DigestAlgorithm
import komple.task.integrity.IntegrityTask
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
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
     *
     * The task must not register any output.
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean = false,
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
    cacheable: Boolean = false,
    noinline configure: T.(inputDirectory: TaskDirectory) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    cacheable = cacheable,
    configure = configure
)

/**
 * Registers a task of type [T] subclass of [IntegrityTask] and [configure]s it.
 *
 * The [IntegrityTask.inputDirectory] is configured before [configure] is invoked.
 */
public inline fun <reified T : IntegrityTask> IntegrityTaskRegistrationScope<*>.integrity(
    cacheable: Boolean = false,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = register<T>(cacheable) { inputDirectory ->
    this.inputDirectory = inputDirectory
    configure?.invoke(this)
}

/**
 * Registers an integrity task that validate the file integrity against [checksum] using
 * [algorithm].
 *
 * It is assumed that only a single file has been downloaded.
 * Note that the returned task is cacheable.
 */
public fun IntegrityTaskRegistrationScope<*>.checksum(
    checksum: Provider<String>,
    algorithm: DigestAlgorithm,
    configure: (ChecksumIntegrityTask.() -> Unit)? = null
): TaskProvider<ChecksumIntegrityTask> = integrity {
    this.checksum = checksum
    this.algorithm = algorithm
    configure?.invoke(this)
}

/**
 * Registers an integrity task that validate the file integrity against [checksum] using
 * [algorithm].
 *
 * It is assumed that only a single file has been downloaded.
 * Note that the returned task is cacheable.
 */
public fun IntegrityTaskRegistrationScope<*>.checksum(
    checksum: String,
    algorithm: DigestAlgorithm,
    configure: (ChecksumIntegrityTask.() -> Unit)? = null
): TaskProvider<ChecksumIntegrityTask> = checksum(
    checksum = providers.provider { checksum },
    algorithm = algorithm,
    configure = configure
)