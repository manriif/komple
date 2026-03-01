package komple.tool

import komple.extension.ExtensionRegistrationScope
import komple.platform.Host
import komple.tasks.DownloadTaskRegistrationScope
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * [KompleTool] contributes to the Gradle project configuration.
 */
public interface KompleTool {

    /**
     * Optional display name of the tool.
     */
    public val displayName: String?

    /**
     * Returns `true` if the current [host] is supported, otherwise returns `false`.
     */
    public fun supportHost(host: Host): Boolean

    /**
     * Extends the base Komple extension by creating tool specific DSL via Gradle extension
     * mechanism.
     */
    public fun ExtensionRegistrationScope.configureExtension()

    /**
     * Registers the task responsible for downloading the tool and returns a [Provider] to the
     * registered task.
     *
     * The returned [Provider] preferably provides a file that it itself obtained from a Gradle
     * task. In other word, a task should be registered here and the task output should be
     * returned as a single [RegularFile].
     *
     * The task output is then used as the input for the [registerExtractTask]
     *
     * Also, an integrity check should ideally be performed on the before the registered task
     * completes.
     */
    public fun DownloadTaskRegistrationScope.registerDownloadTask(): TaskProvider<*>

    /**
     *
     */
    public fun registerExtractTask()
}