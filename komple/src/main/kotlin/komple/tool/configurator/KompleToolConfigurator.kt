package komple.tool.configurator

import komple.platform.Host
import komple.project.ProjectConfigurationScope
import komple.exec.ExecEnvironmentBuilderScope
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.KompleToolExtension
import komple.tool.task.DownloadTaskRegistrationScope
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.InstallTaskRegistrationScope
import komple.tool.task.IntegrityTaskRegistrationScope
import komple.tool.task.TaskRegistrationScope
import org.gradle.api.Named
import org.gradle.api.tasks.TaskProvider

/**
 * [KompleToolConfigurator] contributes to the Gradle project configuration.
 *
 * If [supportHost] returns `false` then [registerDownloadTask] [registerIntegrityTask],
 * [registerExtractTask], [registerInstallTask], [configureProject] and [configureEnvironment]
 * are never called. Inside the body of the previously enumerated functions, it is safe to assume
 * that the [Host] Komple was applied on is supported.
 * Anyway, it is always possible to return [TaskRegistrationScope.unsupported] on complex branch in
 * [registerDownloadTask], [registerIntegrityTask]; [registerExtractTask] and [registerInstallTask].
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
     *
     * Note that if [supportHost] returned `false` then this function is never called for the same
     * [Host] value that was passed to [supportHost]. It is safe to assume that this function is
     * called only for supported host.
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
    // Other
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Populates the execution environment.
     */
    public fun ExecEnvironmentBuilderScope<Ext>.configureEnvironment()

    /**
     * Configures a project.
     */
    public fun ProjectConfigurationScope<Ext>.configureProject()
}