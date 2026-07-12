/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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