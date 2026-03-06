package komple.tool.configurator

import komple.platform.Host
import komple.tool.compile.ExecEnvironmentBuilderScope
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.KompleToolExtension
import komple.tool.task.DownloadTaskRegistrationScope
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.InstallTaskRegistrationScope
import komple.tool.task.IntegrityTaskRegistrationScope
import org.gradle.api.Named
import org.gradle.api.tasks.TaskProvider

/**
 * [KompleToolConfigurator] contributes to the Gradle project configuration.
 */
public interface KompleToolConfigurator<Ext : KompleToolExtension> : Named {

    /**
     * Returns `true` if the current [host] is supported, otherwise returns `false`.
     */
    public fun supportHost(host: Host): Boolean

    ///////////////////////////////////////////////////////////////////////////
    // Installation
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Extends the base Komple extension by creating tool specific DSL [Ext].
     */
    public fun ExtensionConfigurationScope<Ext>.configureExtension(): Ext

    /**
     * Registers the task responsible for downloading the tool and returns a [TaskProvider] to the
     * registered task.
     *
     * The task output is then used as the input for the tasks registered by [registerIntegrityTask]
     * and [registerExtractTask].
     */
    public fun DownloadTaskRegistrationScope<Ext>.registerDownloadTask(): TaskProvider<*>

    /**
     * Registers the task responsible for checking downloaded file(s) integrity and returns a
     * [TaskProvider] to the registered task.
     */
    public fun IntegrityTaskRegistrationScope<Ext>.registerIntegrityTask(): TaskProvider<*>

    /**
     * Registers the task responsible for extracting the downloaded tool file(s) and returns a
     * [TaskProvider] to the registered task.
     *
     * The previously extracted files are deleted first before the task action is executed.
     *
     * The task output is then used as the input for the task registered by [registerInstallTask].
     */
    public fun ExtractTaskRegistrationScope<Ext>.registerExtractTask(): TaskProvider<*>

    /**
     * Registers the task responsible for installing the extracted tool file(s) and returns a
     * [TaskProvider] to the registered task.
     *
     * The previously installed files are deleted first before the task action is executed.
     */
    public fun InstallTaskRegistrationScope<Ext>.registerInstallTask(): TaskProvider<*>

    ///////////////////////////////////////////////////////////////////////////
    // Compilation
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Contributes to the execution environment.
     */
    public fun ExecEnvironmentBuilderScope<Ext>.configureEnvironment()
}