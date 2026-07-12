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