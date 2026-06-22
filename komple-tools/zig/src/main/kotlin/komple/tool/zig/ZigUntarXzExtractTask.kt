package komple.tool.zig

import komple.exec.Command
import komple.task.extract.CommandExtractTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import java.io.File

/**
 * Task responsible for extracting zig from its `tar.xz` on macOS and Linux.
 */
@CacheableTask
internal abstract class ZigUntarXzExtractTask : CommandExtractTask() {

    @get:Input
    abstract val archiveFileName: Property<String>

    override fun buildCommand(
        inputDirectory: File,
        outputDirectory: File
    ): Command {
        val archive = inputDirectory.resolve(archiveFileName.get())

        return Command(
            "tar",
            "-xJf",
            archive.absolutePath,
            "-C",
            outputDirectory.absolutePath,
            "--strip-components=1"
        )
    }
}