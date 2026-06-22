package komple.task.extract

import komple.exec.Command
import komple.task.clearAndGetAsFile
import komple.task.hasChanged
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Base for task extracting content using commandLine.
 */
public abstract class CommandExtractTask : ExtractTask() {

    /**
     * Returns a command used to extract the content.
     *
     * The working directory is set to [inputDirectory] before the command gets executed.
     */
    protected abstract fun buildCommand(
        inputDirectory: File,
        outputDirectory: File
    ): Command

    @TaskAction
    public fun extract() {
        val tracker = tracker.get()

        if (!tracker.hasChanged()) {
            didWork = false
            return logger.lifecycle("Skipping extract command execution")
        }

        val inputDirectory = inputDirectory.get().asFile
        val outputDirectory = fileOperations.clearAndGetAsFile(outputDirectory)

        newCommandExecutor().execute(
            command = buildCommand(inputDirectory, outputDirectory),
            workingDirectory = inputDirectory
        )
    }
}