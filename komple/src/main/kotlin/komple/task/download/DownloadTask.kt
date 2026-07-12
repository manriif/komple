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