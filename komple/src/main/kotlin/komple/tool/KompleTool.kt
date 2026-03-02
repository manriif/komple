package komple.tool

import org.gradle.api.Named
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Configured Komple tool.
 */
public interface KompleTool : Named {

    /**
     * Provider to the task responsible for installing the tool.
     */
    public val installTaskProvider: TaskProvider<*>

    /**
     * Directory where the tool is installed.
     */
    public val installDirectory: Provider<Directory>
}