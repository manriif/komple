package komple.gradle.task

import komple.task.Inputs
import komple.task.ExtractContext
import org.gradle.api.file.Directory

/**
 * Default implementation of [ExtractContext].
 */
internal class DefaultExtractContext(
    outputDirectory: Directory,
    override val inputs: Inputs
) : ExtractContext,
    DefaultTaskContext(outputDirectory)