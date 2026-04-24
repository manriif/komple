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
     * Provider of the task responsible for installing the tool.
     */
    public val installTaskProvider: TaskProvider<*>

    /**
     * Directory where the tool is installed.
     */
    public val installDirectory: Provider<Directory>

    /**
     * Makes this tool depends on [other].
     *
     * This implies that:
     * - [other] is installed when `this` tool is required.
     * - [other] contributes to the environment of `this` tool.
     */
    public fun dependsOn(other: KompleTool)
}