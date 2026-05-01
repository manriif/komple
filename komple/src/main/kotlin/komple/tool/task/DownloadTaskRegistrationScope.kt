package komple.tool.task

import de.undercouch.gradle.tasks.download.DownloadAction
import komple.tool.extension.KompleToolExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
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
 * Registers a download task downloading a single file at [url].
 */
public fun DownloadTaskRegistrationScope<*>.url(
    url: Provider<String>,
    cacheable: Boolean = false,
): TaskProvider<*> = register<DefaultTask>(cacheable) { context ->
    inputs.property("url", url)

    val fileName = url.map { link ->
        link.substringAfterLast('/')
    }

    val destination = context.outputDirectory.file(fileName)

    val download = DownloadAction(project, this).apply {
        dest(destination)
        src(url)
        overwrite(false)
        quiet(false)
    }

    doLast {
        download.execute(true)
    }
}

/**
 * Registers a download task downloading a single file at [url].
 */
public fun DownloadTaskRegistrationScope<*>.url(url: String): TaskProvider<*> =
    url(providers.provider { url })