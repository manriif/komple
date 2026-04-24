package komple.tool.task

import org.gradle.api.file.Directory

/**
 * Context for extracted file(s) installation.
 */
public interface InstallTaskContext : ExecTaskContext {

    /**
     * Directory containing extracted file(s).
     */
    public val extractDirectory: TaskDirectory

    /**
     * Directory where installed file(s) must be written to.
     */
    override val outputDirectory: Directory
}