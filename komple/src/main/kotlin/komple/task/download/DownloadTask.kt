package komple.task.download

import komple.task.OutputToolTask
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Base for download task.
 * TODO logging
 */
public abstract class DownloadTask : OutputToolTask() {

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