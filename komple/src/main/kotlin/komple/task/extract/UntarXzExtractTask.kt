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
package komple.task.extract

import komple.task.singleFile
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileTree
import org.gradle.api.resources.ReadableResource
import org.gradle.api.resources.internal.ReadableResourceInternal
import org.gradle.api.tasks.CacheableTask
import org.tukaani.xz.XZInputStream
import java.io.File
import java.io.InputStream
import java.net.URI

/**
 * Task extracting files from a `.tar.xz` archive file.
 */
@CacheableTask
public abstract class UntarXzExtractTask : UnarchiveExtractTask() {

    /**
     * Returns the archive file.
     * Default to the single file present in [inputDirectory].
     */
    protected open fun getTarXzFile(inputDirectory: File): File = inputDirectory.singleFile

    final override fun ArchiveOperations.createTree(inputDirectory: File): FileTree =
        tarTree(XzResourceInternal(getTarXzFile(inputDirectory)))

    ///////////////////////////////////////////////////////////////////////////
    // Resources
    ///////////////////////////////////////////////////////////////////////////

    /**
     * [ReadableResource] that read a [XZInputStream].
     */
    private open class XzResource(protected val file: File) : ReadableResource {

        override fun read(): InputStream = XZInputStream(file.inputStream())

        override fun getDisplayName(): String = file.absolutePath

        override fun getURI(): URI = file.toURI()

        override fun getBaseName(): String = file.name.removeSuffix(".gz")
    }

    /**
     * Well, [ArchiveOperations.tarTree] seems to accept a [ReadableResource] but Gradle internally
     * expects an [ReadableResourceInternal] implementation.
     */
    private class XzResourceInternal(file: File) :
        ReadableResourceInternal,
        XzResource(file) {

        override fun getBackingFile(): File = file
    }
}