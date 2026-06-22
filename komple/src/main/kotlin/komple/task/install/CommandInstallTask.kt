package komple.task.install

import komple.exec.Command
import komple.task.clearAndGetAsFile
import komple.task.hasChanged
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Base for task configuring a tool using commandLine.
 */
public abstract class CommandInstallTask : InstallTask() {

    /**
     * Returns a command used to install the tool.
     *
     * The extracted content is first copied into [outputDirectory].
     * The working directory is set to [outputDirectory] before the command gets executed.
     */
    protected abstract fun buildCommand(outputDirectory: File): Command

    @TaskAction
    internal fun install() {
        val tracker = tracker.get()

        if (!tracker.hasChanged()) {
            didWork = false
            return logger.lifecycle("Skipping install command execution")
        }

        val inputDirectory = inputDirectory.get().asFile
        val outputDirectory = fileOperations.clearAndGetAsFile(outputDirectory)

        fileOperations.copy {
            from(inputDirectory)
            into(outputDirectory)
        }

        newCommandExecutor().execute(
            command = buildCommand(outputDirectory),
            workingDirectory = outputDirectory
        )
    }
}