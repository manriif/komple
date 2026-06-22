package komple.tool.task

import komple.task.download.DownloadTask
import komple.task.download.UrlDownloadTask
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Scope for download task registration.
 */
public interface DownloadTaskRegistrationScope<Extension : KompleToolExtension> :
    TaskRegistrationScope<Extension> {

    /**
     * Registers a task of type [T], [configure]s it and returns that registered task.
     *
     * The directory where downloaded file(s) must be written to can be obtained from the
     * [DownloadTaskContext] passed to [configure].
     *
     * The task must not register any output and instead use the context directory to put all
     * its files.
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean = false,
        configure: T.(context: DownloadTaskContext) -> Unit
    ): TaskProvider<T>

    /**
     * Registers a task that skips download, assuming the tool is already available on host.
     */
    override fun skip(): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T], [configure]s it and returns that registered task.
 *
 * The directory where downloaded file(s) must be written to can be obtained from the
 * [DownloadTaskContext] passed to [configure].
 */
public inline fun <reified T : Task> DownloadTaskRegistrationScope<*>.register(
    cacheable: Boolean = false,
    noinline configure: T.(context: DownloadTaskContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    cacheable = cacheable,
    configure = configure
)

/**
 * Registers a task of type [T] subclass of [DownloadTask] and [configure]s it.
 *
 * The [DownloadTask.context] is configured before [configure] is invoked.
 */
public inline fun <reified T : DownloadTask> DownloadTaskRegistrationScope<*>.download(
    cacheable: Boolean = false,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = register<T>(cacheable) { context ->
    this.context = context
    configure?.invoke(this)
}

/**
 * Registers a download task downloading a single file named after [fileName] at [url].
 *
 * Note that the returned task is cacheable.
 */
public fun DownloadTaskRegistrationScope<*>.url(
    url: Provider<String>,
    fileName: Provider<String> = url.map { it.substringAfterLast('/') },
    configure: (UrlDownloadTask.() -> Unit)? = null
): TaskProvider<UrlDownloadTask> = download {
    this.url = url
    this.fileName = fileName
    configure?.invoke(this)
}

/**
 * Registers a download task downloading a single file named after [fileName] at [url].
 *
 * Note that the returned task is cacheable.
 */
public fun DownloadTaskRegistrationScope<*>.url(
    url: String,
    fileName: String = url.substringAfterLast('/'),
    configure: (UrlDownloadTask.() -> Unit)? = null
): TaskProvider<UrlDownloadTask> = url(
    url = providers.provider { url },
    fileName = providers.provider { fileName },
    configure = configure
)