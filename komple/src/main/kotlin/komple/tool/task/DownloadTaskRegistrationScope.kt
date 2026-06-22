package komple.tool.task

import komple.task.download.DownloadTask
import komple.task.download.UrlDownloadTask
import komple.task.enableTracking
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
    TaskRegistrationScope<Extension, DownloadTaskContext> {

    /**
     * Registers a task of type [T], [configure]s it and returns it.
     *
     * The directory where downloaded file(s) must be written to can be obtained from the
     * [DownloadTaskContext.outputDirectory] passed to [configure]. That directory must be
     * registered as the task's only output directory.
     */
    public override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
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
 * Registers a task of type [T], [configure]s it and returns it.
 *
 * The directory where downloaded file(s) must be written to can be obtained from the
 * [DownloadTaskContext.outputDirectory] passed to [configure]. That directory must be
 * registered as the task's only output directory.
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
 * The [DownloadTask.execEnvironment], [DownloadTask.tracker] and [DownloadTask.outputDirectory]
 * properties are configured before [configure] is invoked.
 *
 * Tracking is enabled by default.
 */
public inline fun <reified T : DownloadTask> DownloadTaskRegistrationScope<*>.download(
    cacheable: Boolean = false,
    noinline configure: (T.(context: DownloadTaskContext) -> Unit)? = null
): TaskProvider<T> = outputTool(cacheable) { context ->
    context.tracker.enableTracking()
    configure?.invoke(this, context)
}

/**
 * Registers a download task downloading a single file named after [fileName] at [url].
 *
 * Tracking is enabled by default.
 * Note that the returned task is cacheable.
 */
public fun DownloadTaskRegistrationScope<*>.url(
    url: Provider<String>,
    fileName: Provider<String> = url.map { it.substringAfterLast('/') },
    configure: (UrlDownloadTask.(context: DownloadTaskContext) -> Unit)? = null
): TaskProvider<UrlDownloadTask> = download { context ->
    this.url = url
    this.fileName = fileName
    configure?.invoke(this, context)
}

/**
 * Registers a download task downloading a single file named after [fileName] at [url].
 *
 * Tracking is enabled by default.
 * Note that the returned task is cacheable.
 */
public fun DownloadTaskRegistrationScope<*>.url(
    url: String,
    fileName: String = url.substringAfterLast('/'),
    configure: (UrlDownloadTask.(context: DownloadTaskContext) -> Unit)? = null
): TaskProvider<UrlDownloadTask> = url(
    url = providers.provider { url },
    fileName = providers.provider { fileName },
    configure = configure
)