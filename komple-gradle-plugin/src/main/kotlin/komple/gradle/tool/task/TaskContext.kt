package komple.gradle.tool.task

import komple.tool.task.DownloadTaskContext
import komple.tool.task.ExtractTaskContext
import komple.tool.task.InstallTaskContext
import komple.tool.task.TaskContext
import komple.tool.task.TaskDirectory
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

/**
 * Base for implementors of [TaskContext].
 */
internal abstract class DefaultTaskContext(
    override val outputDirectory: Directory,
    override val outputChanged: Provider<Boolean>
) : TaskContext

/**
 * Default implementation of [DownloadTaskContext].
 */
internal class DefaultDownloadTaskContext(
    outputDirectory: Directory,
    outputChanged: Provider<Boolean>
) : DownloadTaskContext,
    DefaultTaskContext(
        outputDirectory = outputDirectory,
        outputChanged = outputChanged
    )

/**
 * Default implementation of [ExtractTaskContext].
 */
internal class DefaultExtractTaskContext(
    override val downloadDirectory: TaskDirectory,
    outputDirectory: Directory,
    outputChanged: Provider<Boolean>
) : ExtractTaskContext,
    DefaultTaskContext(
        outputDirectory = outputDirectory,
        outputChanged = outputChanged
    )

/**
 * Default implementation of [InstallTaskContext].
 */
internal class DefaultInstallTaskContext(
    override val extractDirectory: TaskDirectory,
    outputDirectory: Directory,
    outputChanged: Provider<Boolean>
) : InstallTaskContext,
    DefaultTaskContext(
        outputDirectory = outputDirectory,
        outputChanged = outputChanged
    )