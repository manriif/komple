package komple.task.extract

import komple.exec.Bash
import komple.exec.execOutput
import komple.exec.invoke
import komple.task.clearAndGetAsFile
import komple.task.hasChanged
import komple.task.singleFile
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Base for task extracting content from a `.dmg` file.
 *
 * The task performs the below operations:
 * 1. Mount the DMG
 * 2. Invokes [extractContent] with the path to the mounted image.
 * 3. Unmount the DMG
 */
public abstract class DmgExtractTask : ExtractTask() {

    /**
     * Extracts files from DMG [mountPoint] unto [outputDirectory].
     */
    protected abstract fun FileSystemOperations.extractContent(
        mountPoint: File,
        outputDirectory: File
    )

    @TaskAction
    internal fun extract() {
        val tracker = tracker.get()

        if (!tracker.hasChanged()) {
            didWork = false
            return logger.lifecycle("Skipping DMG extraction")
        }

        val inputDirectory = inputDirectory.get().asFile
        val outputDirectory = fileOperations.clearAndGetAsFile(outputDirectory)
        val dmgPath = inputDirectory.singleFile.absolutePath

        val mountPoint = execOperations.execOutput(
            Bash("hdiutil", "attach", dmgPath, "-nobrowse", "-plist") {
                pipe(
                    "xpath",
                    "-e",
                    """'//key[.="mount-point"]/following-sibling::string[1]/text()'""",
                    "2>/dev/null"
                )

                pipe("tail", "-1")
            }
        )

        try {
            fileOperations.extractContent(
                File(mountPoint),
                outputDirectory
            )
        } finally {
            execOperations.exec {
                commandLine("hdiutil", "detach", mountPoint)
            }
        }
    }
}