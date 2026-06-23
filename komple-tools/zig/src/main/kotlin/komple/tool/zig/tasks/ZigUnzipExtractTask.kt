package komple.tool.zig.tasks

import komple.task.extract.UnzipExtractTask
import org.gradle.api.tasks.CacheableTask
import java.io.File

/**
 * Task responsible for unzipping zig on Windows.
 */
@CacheableTask
internal abstract class ZigUnzipExtractTask : UnzipExtractTask(), ZigTask {

    override fun getZipFile(inputDirectory: File): File =
        inputDirectory.resolve(archiveFileName.get())
}