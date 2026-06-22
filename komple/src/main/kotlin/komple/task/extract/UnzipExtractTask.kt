package komple.task.extract

import komple.task.singleFile
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.CacheableTask
import java.io.File

/**
 * Task extracting files from a `.zip` archive file.
 */
@CacheableTask
public abstract class UnzipExtractTask : UnarchiveExtractTask() {

    override fun ArchiveOperations.createTree(inputDirectory: File): FileTree =
        zipTree(inputDirectory.singleFile)
}