package komple.task.install

import komple.exec.Command
import komple.exec.createCommandExecutor
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

/**
 * Base for task configuring a tool using commandLine.
 */
public abstract class CommandInstallTask : InstallTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    /**
     * Returns a command used to install the tool.
     *
     * The extracted content is first copied into [outputDirectory].
     * The working directory is set to [outputDirectory] before the command gets executed.
     */
    protected abstract fun buildCommand(outputDirectory: File): Command

    @TaskAction
    public fun install() {
        val context = context.get()
        val outputDirectory = context.outputDirectory.asFile

        fileOperations.copy {
            from(context.extractDirectory.directory)
            into(context.outputDirectory)
        }

        context.execEnvironment
            .createCommandExecutor(execOperations)
            .execute(
                command = buildCommand(outputDirectory),
                workingDirectory = outputDirectory
            )
    }
}