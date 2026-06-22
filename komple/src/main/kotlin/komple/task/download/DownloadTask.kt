package komple.task.download

import komple.tool.task.DownloadTaskContext
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.internal.logging.progress.ProgressLogger
import org.gradle.internal.logging.progress.ProgressLoggerFactory
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.inject.Inject

/**
 * Base for download task.
 * TODO logging
 */
public abstract class DownloadTask : DefaultTask() {

    @get:Inject
    internal abstract val progressLoggerFactory: ProgressLoggerFactory

    @get:Nested
    public abstract val context: Property<DownloadTaskContext>

    /**
     * Returns a [ProgressLogger] instance.
     */
    private fun createProgressLogger(): ProgressLogger =
        progressLoggerFactory.newOperation(this::class.java)

    /**
     * Downloads a file, from [url], into [file].
     * An exception is thrown if the download fails.
     */
    protected fun HttpClient.downloadFile(
        file: File,
        url: String
    ) {
        val fileRequest = HttpRequest
            .newBuilder(URI.create(url))
            .build()

        val fileResponse = send(fileRequest, HttpResponse.BodyHandlers.ofFile(file.toPath()))
        val statusCode = fileResponse.statusCode()

        check(statusCode == 200) {
            "Downloading of $url failed with status code $statusCode"
        }
    }
}