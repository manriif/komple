package komple.tool

import komple.extension.ExtensionConfigurationScope
import komple.platform.Host
import komple.task.DownloadTaskRegistrationScope
import komple.task.ExtractTaskRegistrationScope
import komple.task.IntegrityTaskRegistrationScope
import org.gradle.api.Named
import org.gradle.api.tasks.TaskProvider

/**
 * [KompleToolConfigurator] contributes to the Gradle project configuration.
 */
public interface KompleToolConfigurator : Named {

    /**
     * Returns `true` if the current [host] is supported, otherwise returns `false`.
     */
    public fun supportHost(host: Host): Boolean

    /**
     * Extends the base Komple extension by creating tool specific DSL via Gradle extension
     * mechanism.
     */
    public fun ExtensionConfigurationScope.configureExtension()

    /**
     * Registers the task responsible for downloading the tool and returns a [TaskProvider] to the
     * registered task.
     *
     * The task output is then used as the input for the tasks registered by [registerIntegrityTask]
     * and [registerExtractTask].
     */
    public fun DownloadTaskRegistrationScope.registerDownloadTask(): TaskProvider<*>

    /**
     * Registers the task responsible for checking downloaded file(s) integrity and returns a
     * [TaskProvider] to the registered task.
     */
    public fun IntegrityTaskRegistrationScope.registerIntegrityTask(): TaskProvider<*>

    /**
     * Registers the task responsible for extracting the downloaded tool file(s) and returns a
     * [TaskProvider] to the registered task.
     *
     * The previously extracted files are deleted first before the task action is executed.
     *
     * The task output is then used as the input for the task registered by [registerInstallTask].
     */
    public fun ExtractTaskRegistrationScope.registerExtractTask(): TaskProvider<*>
}