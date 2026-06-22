package komple.tool.task

import org.gradle.api.file.Directory
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory

/**
 * Context for downloaded file(s) extraction.
 */
public interface ExtractTaskContext : ExecToolTaskContext {

    /**
     * Directory containing file(s) getting downloaded.
     */
    @get:Nested
    public val downloadDirectory: TaskDirectory

    /**
     * Directory where extracted file(s) must be written to.
     */
    @get:OutputDirectory
    override val outputDirectory: Directory
}