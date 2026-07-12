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
package komple.task.install

import komple.exec.Command
import komple.task.clearAndGetAsFile
import komple.task.hasChanged
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Base for task configuring a tool using commandLine.
 */
public abstract class CommandInstallTask : InstallTask() {

    /**
     * Returns a command used to install the tool.
     *
     * The extracted content is first copied into [outputDirectory].
     * The working directory is set to [outputDirectory] before the command gets executed.
     */
    protected abstract fun buildCommand(outputDirectory: File): Command

    @TaskAction
    internal fun install() {
        val tracker = tracker.get()

        if (!tracker.hasChanged()) {
            didWork = false
            return logger.lifecycle("Skipping install command execution")
        }

        val inputDirectory = inputDirectory.get().asFile
        val outputDirectory = fileOperations.clearAndGetAsFile(outputDirectory)

        fileOperations.copy {
            from(inputDirectory)
            into(outputDirectory)
        }

        newCommandExecutor().execute(
            command = buildCommand(outputDirectory),
            workingDirectory = outputDirectory
        )
    }
}