package komple.task

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Scope for download task registration.
 */
public interface DownloadTaskRegistrationScope : TaskRegistrationScope {

    /**
     * Registers a task of type [T], [configure]s it and returns it.
     * The task must output the downloaded file(s).
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: DownloadContext) -> Unit
    ): TaskProvider<T>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T], [configure]s it and returns it.
 * The task must output the downloaded file(s).
 */
public inline fun <reified T : Task> DownloadTaskRegistrationScope.register(
    noinline configure: T.(context: DownloadContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    configure = configure
)

/**
 * Registers a download task downloading a single file at [url].
 */
public fun DownloadTaskRegistrationScope.url(
    url: Provider<String>
): TaskProvider<*> = register<Download> { context ->
    val fileExtension = url.map { it.substringAfterLast('.') }

    val fileName = fileExtension.map { extension ->
        "${toolName}${extension.takeIf { it.isNotEmpty() }?.let { ".$it" }.orEmpty()}"
    }

    val destination = context.outputDirectory.file(fileName)

    inputs.property("url", url)
    outputs.file(destination)

    dest(destination)
    src(url)
    overwrite(false)
    quiet(false)
}

/**
 * Registers a download task downloading a single file at [url].
 */
public fun DownloadTaskRegistrationScope.url(url: String): TaskProvider<*> =
    url(providers.provider { url })