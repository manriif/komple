package komple.task.download

import komple.task.clearAndGetAsFile
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
    internal fun download() {
        val tracker = tracker.get()

        if (!tracker.hasChanged()) {
            didWork = false
            return logger.lifecycle("Reusing previously downloaded file")
        }

        val outputDirectory = fileOperations.clearAndGetAsFile(outputDirectory)
        val fileName = fileName.get()
        val url = url.get()
        val file = outputDirectory.resolve(fileName)

        val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()

        logger.lifecycle("Downloading $fileName from $url")

        try {
            client.downloadFile(file, url)
            logger.lifecycle("Successfully downloaded file $fileName")
        } catch (exception: Throwable) {
            logger.error("Failed to download file $fileName", exception)
        }
    }
}