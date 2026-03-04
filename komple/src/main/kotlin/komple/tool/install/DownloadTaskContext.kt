package komple.tool.install

import org.gradle.api.file.Directory

/**
 * Context for files downloading.
 */
public interface DownloadTaskContext : TaskContext {

    /**
     * Directory that should preferably be used to write the extracted file(s) to.
     */
    override val outputDirectory: Directory
}