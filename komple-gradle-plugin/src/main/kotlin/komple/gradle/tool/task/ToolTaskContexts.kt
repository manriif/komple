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
    override val inputDirectory: Provider<Directory>,
    execEnvironmentProvider: ExecEnvironmentProvider
) : InstallTaskContext,
    DefaultOutputTaskContext(
        tracker = tracker,
        outputDirectory = outputDirectory,
        execEnvironmentProvider = execEnvironmentProvider
    )