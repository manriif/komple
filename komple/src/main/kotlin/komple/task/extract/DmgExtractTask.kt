package komple.task.extract

import komple.exec.Bash
import komple.exec.execOutput
import komple.exec.invoke
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

/**
 * Base for task extracting content from a `.dmg` file.
 *
 * The task performs the below operations:
 * 1. Mount the DMG
 * 2. Invokes [extractContent] with the path to the mounted image.
 * 3. Unmount the DMG
 */
public abstract class DmgExtractTask : ExtractTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    /**
     * Extracts files from DMG [mountPoint] unto [outputDirectory].
     */
    protected abstract fun FileSystemOperations.extractContent(
        mountPoint: File,
        outputDirectory: File
    )

    @TaskAction
    public fun extract() {
        val context = context.get()
        val dmgPath = context.downloadDirectory.singleFile

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
                context.outputDirectory.asFile
            )
        } finally {
            execOperations.exec {
                commandLine("hdiutil", "detach", mountPoint)
            }
        }
    }
}