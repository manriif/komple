package komple.tool.task

import org.gradle.api.file.Directory
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory

/**
 * Context for extracted file(s) installation.
 */
public interface InstallTaskContext : ExecToolTaskContext {

    /**
     * Directory containing extracted file(s).
     */
    @get:Nested
    public val extractDirectory: TaskDirectory

    /**
     * Directory where installed file(s) must be written to.
     */
    @get:OutputDirectory
    override val outputDirectory: Directory
}