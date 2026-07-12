/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package komple.tool.configurator

import komple.exec.ShellEnvironmentBuilderScope
import komple.platform.Host
import komple.project.ProjectConfigurationScope
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
 * are never called.
 *
 * Inside the body of the previously enumerated functions, it is safe to assume
 * that the [Host] Komple was applied on is supported. Anyway, it is always possible to return
 * [TaskRegistrationScope.unsupported] on complex branch in [registerDownloadTask],
 * [registerIntegrityTask], [registerExtractTask] and [registerInstallTask].
 */
public interface KompleToolConfigurator<Ext : KompleToolExtension> : Named {

    /**
     * Returns `true` if the current [host] is supported, otherwise returns `false`.
     */
    public fun supportHost(host: Host): Boolean

    ///////////////////////////////////////////////////////////////////////////
    // Configuration
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
     *
     * Note that integrity check can be performed in the download task. Integrity checking is
     * recommended if the file had been download from an external source.
     */
    public fun IntegrityTaskRegistrationScope<Ext>.registerIntegrityTask(): TaskProvider<*>

    /**
     * Registers the task responsible for extracting the downloaded tool file(s) and returns a
     * [TaskProvider] to the registered task.
     *
     * The task output is then used as the input for the task registered by [registerInstallTask].
     */
    public fun ExtractTaskRegistrationScope<Ext>.registerExtractTask(): TaskProvider<*>

    /**
     * Registers the task responsible for installing the extracted tool and returns a [TaskProvider]
     * to the registered task.
     *
     * If [registerExtractTask] has returned [ExtractTaskRegistrationScope.skip] then the input
     * file(s) to the installation task are those produced by the task returned by
     * [registerDownloadTask].
     */
    public fun InstallTaskRegistrationScope<Ext>.registerInstallTask(): TaskProvider<*>

    ///////////////////////////////////////////////////////////////////////////
    // Shell
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Populates a shell environment by registering any useful environment variable, path to
     * executable or command configuring the shell.
     *
     * Execution environment are used by other tools and projects.
     */
    public fun ShellEnvironmentBuilderScope<Ext>.configureEnvironment()

    ///////////////////////////////////////////////////////////////////////////
    // Projects
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Configures a project.
     */
    public fun ProjectConfigurationScope<Ext>.configureProject()
}