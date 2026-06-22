package komple.tool.zig

import komple.task.extract.UnarchiveExtractTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileTree
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import java.io.File

/**
 * Task responsible for unzipping zig on Windows.
 */
@CacheableTask
internal abstract class ZigUnzipExtractTask : UnarchiveExtractTask() {

    @get:Input
    abstract val archiveFileName: Property<String>

    override fun ArchiveOperations.createTree(inputDirectory: File): FileTree =
        zipTree(inputDirectory.resolve(archiveFileName.get()))
}