package komple.tool.task

import org.gradle.api.file.Directory
import org.gradle.api.tasks.OutputDirectory

/**
 * Context for files downloading.
 */
public interface DownloadTaskContext : ExecToolTaskContext {

    /**
     * Directory where downloaded file(s) must be written to.
     */
    @get:OutputDirectory
    override val outputDirectory: Directory
}