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
        configure: T.(context: DownloadTaskContext) -> Unit
    ): TaskProvider<T>
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
    noinline configure: T.(context: DownloadTaskContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    configure = configure
)

/**
 * Registers a download task downloading a single file at [url].
 */
public fun DownloadTaskRegistrationScope<*>.url(
    url: Provider<String>
): TaskProvider<*> = register<DefaultTask> { context ->
    inputs.property("url", url)

    val fileExtension = url.map { it.substringAfterLast('.') }

    val fileName = fileExtension.map { extension ->
        "${toolName}${extension.takeIf { it.isNotEmpty() }?.let { ".$it" }.orEmpty()}"
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