package komple.gradle.task

import komple.task.Inputs
import komple.task.InstallContext
import org.gradle.api.file.Directory

/**
 * Default implementation of [InstallContext].
 */
internal class DefaultInstallContext(
    outputDirectory: Directory,
    override val inputs: Inputs
) : InstallContext,
    DefaultTaskContext(outputDirectory)