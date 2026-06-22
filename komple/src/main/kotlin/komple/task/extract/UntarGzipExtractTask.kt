package komple.task.extract

import komple.tool.task.TaskDirectory
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.CacheableTask

/**
 * Task extracting files from a `.tar.gz` archive file.
 */
@CacheableTask
public abstract class UntarGzipExtractTask : UnarchiveExtractTask() {

    override fun ArchiveOperations.createTree(inputDirectory: TaskDirectory): FileTree =
        tarTree(gzip(inputDirectory.singleFile))
}