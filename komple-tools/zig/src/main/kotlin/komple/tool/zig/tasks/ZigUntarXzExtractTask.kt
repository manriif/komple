package komple.tool.zig.tasks

import komple.task.extract.UntarXzExtractTask
import org.gradle.api.tasks.CacheableTask
import java.io.File

/**
 * Task responsible for extracting zig from its `tar.xz` on macOS and Linux.
 */
@CacheableTask
internal abstract class ZigUntarXzExtractTask : UntarXzExtractTask(), ZigTask {

    override fun getTarXzFile(inputDirectory: File): File =
        inputDirectory.resolve(archiveFileName.get())
}