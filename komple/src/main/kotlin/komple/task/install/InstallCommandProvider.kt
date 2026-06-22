package komple.task.install

import komple.exec.Command
import komple.tool.task.TaskDirectory
import java.io.File

/**
 * Provides an extraction command.
 */
public fun interface InstallCommandProvider {

    /**
     * Returns a command used to install the tool.
     *
     * The extracted content is first copied into [outputDirectory].
     * The working directory is set to [outputDirectory] before the command gets executed.
     */
    public fun createCommand(outputDirectory: File): Command
}