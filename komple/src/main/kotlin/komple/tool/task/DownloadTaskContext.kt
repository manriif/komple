package komple.tool.task

import org.gradle.api.file.Directory

/**
 * Context for tool file(s) downloading.
 */
public interface DownloadTaskContext : OutputToolTaskContext {

    /**
     * Directory where downloaded file(s) must be written to.
     */
    override val outputDirectory: Directory
}