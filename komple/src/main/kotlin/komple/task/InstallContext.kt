package komple.task

import org.gradle.api.file.Directory

/**
 * Context for extracted file(s) installation.
 */
public interface InstallContext : TaskContext {

    /**
     * File(s) getting extracted.
     */
    public val inputs: Inputs

    /**
     * Directory that should preferably be used to write the installed file(s) to.
     */
    override val outputDirectory: Directory
}