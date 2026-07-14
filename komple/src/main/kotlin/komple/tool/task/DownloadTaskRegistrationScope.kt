/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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