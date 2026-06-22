package komple.task.extract

import komple.exec.Command
import komple.tool.task.TaskDirectory
import java.io.File

/**
 * Provides an extraction command.
 */
public fun interface ExtractCommandProvider {

    /**
     * Returns the command used to extract content from [inputDirectory] into [outputDirectory].
     *
     * The working directory is set to [inputDirectory] before the command gets executed.
     */
    public fun createCommand(
        inputDirectory: TaskDirectory,
        outputDirectory: File
    ): Command
}