package komple.tool.task

import org.gradle.api.file.Directory

/**
 * Context for files downloading.
 */
public interface DownloadTaskContext : TaskContext {

    /**
     * Directory where downloaded file(s) must be written to.
     */
    override val outputDirectory: Directory
}