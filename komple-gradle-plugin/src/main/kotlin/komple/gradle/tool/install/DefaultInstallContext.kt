package komple.gradle.tool.install

import komple.tool.install.Inputs
import komple.tool.install.InstallContext
import org.gradle.api.file.Directory

/**
 * Default implementation of [InstallContext].
 */
internal class DefaultInstallContext(
    outputDirectory: Directory,
    override val inputs: Inputs
) : InstallContext,
    DefaultTaskContext(outputDirectory)