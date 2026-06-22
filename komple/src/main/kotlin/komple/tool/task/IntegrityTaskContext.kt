package komple.tool.task

import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

/**
 * Context for tool file(s) integrity.
 */
public interface IntegrityTaskContext : ToolTaskContext {

    /**
     * Directory containing previous step file(s).
     */
    public val inputDirectory: Provider<Directory>
}