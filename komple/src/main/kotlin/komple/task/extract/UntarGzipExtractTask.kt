package komple.task.extract

import komple.task.singleFile
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.CacheableTask
import java.io.File

/**
 * Task extracting files from a `.tar.gz` archive file.
 */
@CacheableTask
public abstract class UntarGzipExtractTask : UnarchiveExtractTask() {

    /**
     * Returns the archive file.
     * Default to the single file present in [inputDirectory].
     */
    protected open fun getTarGzFile(inputDirectory: File): File = inputDirectory.singleFile

    final override fun ArchiveOperations.createTree(inputDirectory: File): FileTree =
        tarTree(gzip(getTarGzFile(inputDirectory)))
}