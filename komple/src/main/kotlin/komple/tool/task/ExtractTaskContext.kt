package komple.tool.task

import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

/**
 * Context for downloaded file(s) extraction.
 */
public interface ExtractTaskContext : OutputToolTaskContext {

    /**
     * Directory containing previous step file(s).
     */
    public val inputDirectory: Provider<Directory>

    /**
     * Directory where extracted file(s) must be written to.
     */
    override val outputDirectory: Directory
}