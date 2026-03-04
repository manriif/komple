package komple.tool.install

import org.gradle.api.file.Directory

/**
 * Context for extracted file(s) installation.
 */
public interface InstallTaskContext : TaskContext {

    /**
     * File(s) getting extracted.
     */
    public val inputs: Inputs

    /**
     * Directory that should preferably be used to write the installed file(s) to.
     */
    override val outputDirectory: Directory
}