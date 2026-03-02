package komple.task

import org.gradle.api.file.Directory

/**
 * Context for files downloading.
 */
public interface DownloadContext : TaskContext {

    /**
     * Directory that should preferably be used to write the extracted file(s) to.
     */
    override val outputDirectory: Directory
}