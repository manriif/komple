package komple.task.extract

import komple.tool.task.TaskDirectory
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileTree
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
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
    protected abstract fun ArchiveOperations.createTree(inputDirectory: TaskDirectory): FileTree

    @TaskAction
    public fun extract() {
        val context = context.get()

        if (enclosedContent.get()) {
            fileOperations.copy {
                from(archiveOperations.createTree(context.downloadDirectory)) {
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

                into(context.outputDirectory)
            }
        } else {
            fileOperations.copy {
                from(archiveOperations.createTree(context.downloadDirectory))
                into(context.outputDirectory)
            }
        }
    }
}