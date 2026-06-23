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