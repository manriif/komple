package komple.task.download

import komple.task.hasChanged
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.net.http.HttpClient

/**
 * Task downloading a file from a single URL.
 */
@CacheableTask
public abstract class UrlDownloadTask : DownloadTask() {

    /**
     * Name of the file as it will be stored locally.
     */
    @get:Input
    public abstract val fileName: Property<String>

    /**
     * Url of the file to download.
     */
    @get:Input
    public abstract val url: Property<String>

    @TaskAction
    public fun download() {
        val context = context.get()

        if (!context.hasChanged()) {
            didWork = true
            return logger.lifecycle("Reusing cached $fileName")
        }

        val outputDirectory = context.outputDirectory.asFile
        val url = url.get()
        val fileName = fileName.get()
        val file = outputDirectory.resolve(fileName)
        val client = HttpClient.newHttpClient()

        logger.lifecycle("Downloading $fileName from $url")

        try {
            client.downloadFile(file, url)
            didWork = true
            logger.lifecycle("Successfully downloaded file $fileName")
        } catch (exception: Throwable) {
            didWork = false
            logger.error("Failed to download file $fileName", exception)
        }
    }
}