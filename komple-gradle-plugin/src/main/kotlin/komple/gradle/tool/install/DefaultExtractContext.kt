package komple.gradle.tool.install

import komple.tool.install.Inputs
import komple.tool.install.ExtractContext
import org.gradle.api.file.Directory

/**
 * Default implementation of [ExtractContext].
 */
internal class DefaultExtractContext(
    outputDirectory: Directory,
    override val inputs: Inputs
) : ExtractContext,
    DefaultTaskContext(outputDirectory)