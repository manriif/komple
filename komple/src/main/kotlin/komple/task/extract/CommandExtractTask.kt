package komple.task.extract

import komple.exec.Command
import komple.exec.createCommandExecutor
import komple.tool.task.TaskDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

/**
 * Base for task extracting content using commandLine.
 */
public abstract class CommandExtractTask : ExtractTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    /**
     * Returns a command used to extract the content.
     *
     * The working directory is set to [inputDirectory] before the command gets executed.
     */
    protected abstract fun buildCommand(
        inputDirectory: TaskDirectory,
        outputDirectory: File
    ): Command

    @TaskAction
    public fun extract() {
        val context = context.get()

        context.execEnvironment
            .createCommandExecutor(execOperations)
            .execute(
                command = buildCommand(context.downloadDirectory, context.outputDirectory.asFile),
                workingDirectory = context.downloadDirectory.directory.get().asFile
            )
    }
}