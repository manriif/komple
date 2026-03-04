package komple.gradle.tool.install

import komple.tool.install.DownloadTaskContext
import komple.tool.install.ExtractTaskContext
import komple.tool.install.Inputs
import komple.tool.install.InstallTaskContext
import komple.tool.install.TaskContext
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
    outputDirectory: Directory,
    outputChanged: Provider<Boolean>,
    override val inputs: Inputs
) : ExtractTaskContext,
    DefaultTaskContext(
        outputDirectory = outputDirectory,
        outputChanged = outputChanged
    )

/**
 * Default implementation of [InstallTaskContext].
 */
internal class DefaultInstallTaskContext(
    outputDirectory: Directory,
    outputChanged: Provider<Boolean>,
    override val inputs: Inputs
) : InstallTaskContext,
    DefaultTaskContext(
        outputDirectory = outputDirectory,
        outputChanged = outputChanged
    )