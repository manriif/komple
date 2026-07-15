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
package komple.gradle.tool.task

import komple.exec.ExecEnvironment
import komple.gradle.exec.ExecEnvironmentProvider
import komple.gradle.platform.CurrentHost
import komple.platform.Host
import komple.task.TaskStateTracker
import komple.tool.task.DownloadTaskContext
import komple.tool.task.ExtractTaskContext
import komple.tool.task.InstallTaskContext
import komple.tool.task.IntegrityTaskContext
import komple.tool.task.OutputToolTaskContext
import komple.tool.task.ToolTaskContext
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

/**
 * Base for implementors of [ToolTaskContext].
 */
internal abstract class DefaultTaskContext(
    private val execEnvironmentProvider: ExecEnvironmentProvider
) : ToolTaskContext {

    override val host: Host
        get() = CurrentHost

    override val execEnvironment: ExecEnvironment
        get() = execEnvironmentProvider.get()
}

/**
 * Default implementation of [ExtractTaskContext].
 */
internal abstract class DefaultOutputTaskContext(
    override val tracker: TaskStateTracker,
    override val outputDirectory: Directory,
    execEnvironmentProvider: ExecEnvironmentProvider
) : OutputToolTaskContext,
    DefaultTaskContext(execEnvironmentProvider)

/**
 * Default implementation of [DownloadTaskContext].
 */
internal class DefaultDownloadTaskContext(
    tracker: TaskStateTracker,
    outputDirectory: Directory,
    execEnvironmentProvider: ExecEnvironmentProvider
) : DownloadTaskContext,
    DefaultOutputTaskContext(
        tracker = tracker,
        outputDirectory = outputDirectory,
        execEnvironmentProvider = execEnvironmentProvider
    )

/**
 * Default implementation of [IntegrityTaskContext].
 */
internal class DefaultIntegrityTaskContext(
    override val inputDirectory: Provider<Directory>,
    execEnvironmentProvider: ExecEnvironmentProvider
) : IntegrityTaskContext,
    DefaultTaskContext(execEnvironmentProvider)

/**
 * Default implementation of [ExtractTaskContext].
 */
internal class DefaultExtractTaskContext(
    tracker: TaskStateTracker,
    outputDirectory: Directory,
    override val inputDirectory: Provider<Directory>,
    execEnvironmentProvider: ExecEnvironmentProvider
) : ExtractTaskContext,
    DefaultOutputTaskContext(
        tracker = tracker,
        outputDirectory = outputDirectory,
        execEnvironmentProvider = execEnvironmentProvider
    )

/**
 * Default implementation of [InstallTaskContext].
 */
internal class DefaultInstallTaskContext(
    tracker: TaskStateTracker,
    outputDirectory: Directory,
    override val cacheDirectory: Provider<Directory>,
    override val inputDirectory: Provider<Directory>,
    execEnvironmentProvider: ExecEnvironmentProvider
) : InstallTaskContext,
    DefaultOutputTaskContext(
        tracker = tracker,
        outputDirectory = outputDirectory,
        execEnvironmentProvider = execEnvironmentProvider
    )