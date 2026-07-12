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

import komple.task.clearAndGetAsFile
import komple.task.hasChanged
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileTree
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

/**
 * Base for extraction tasks manipulating archives.
 */
public abstract class UnarchiveExtractTask : ExtractTask() {

    @get:Inject
    protected abstract val archiveOperations: ArchiveOperations

    /**
     * If [enclosedContent] is `true` then the contents inside the root directory is moved to the
     * root and the empty root directory is excluded.
     */
    @get:Input
    public abstract val enclosedContent: Property<Boolean>

    /**
     * Returns a [FileTree] containing all the files to extract.
     */
    protected abstract fun ArchiveOperations.createTree(inputDirectory: File): FileTree

    @TaskAction
    internal fun extract() {
        val tracker = tracker.get()

        if (!tracker.hasChanged()) {
            didWork = false
            return logger.lifecycle("Skipping archive extraction")
        }

        val inputDirectory = inputDirectory.get().asFile
        val outputDirectory = fileOperations.clearAndGetAsFile(outputDirectory)

        if (enclosedContent.get()) {
            fileOperations.copy {
                from(archiveOperations.createTree(inputDirectory)) {
                    eachFile {
                        val segments = relativePath.segments

                        if (segments.size > 1) {
                            relativePath = RelativePath(true, *segments.drop(1).toTypedArray())
                        } else {
                            exclude()
                        }
                    }

                    includeEmptyDirs = false
                }

                into(outputDirectory)
            }
        } else {
            fileOperations.copy {
                from(archiveOperations.createTree(inputDirectory))
                into(outputDirectory)
            }
        }
    }
}