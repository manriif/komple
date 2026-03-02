package komple.task

import org.gradle.api.file.Directory

/**
 * Context for downloaded file(s) extraction.
 */
public interface ExtractContext : TaskContext {

    /**
     * File(s) getting downloaded.
     */
    public val inputs: Inputs

    /**
     * Directory that should preferably be used to write the extracted file(s) to.
     */
    override val outputDirectory: Directory
}