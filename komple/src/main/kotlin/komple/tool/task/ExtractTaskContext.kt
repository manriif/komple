package komple.tool.task

import org.gradle.api.file.Directory

/**
 * Context for downloaded file(s) extraction.
 */
public interface ExtractTaskContext : TaskContext {

    /**
     * Directory containing file(s) getting downloaded.
     */
    public val downloadDirectory: TaskDirectory

    /**
     * Directory where extracted file(s) must be written to.
     */
    override val outputDirectory: Directory
}