package komple.gradle.exec

import komple.exec.Command
import komple.exec.CommandExecutor
import komple.exec.CommandInterpreter
import komple.exec.commandLine
import komple.exec.execOutput
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File
import java.io.Serializable

/**
 * Default implementation of [CommandExecutor].
 */
internal class DefaultCommandExecutor(
    private val execOperations: ExecOperations,
    private val parameters: Parameters
) : CommandExecutor {

    /**
     * Configures the execution environment and sets the final command line.
     */
    private fun ExecSpec.configure(
        mainCommand: Command,
        workingDirectory: File?
    ) {
        workingDirectory?.let { workingDir = it }

        parameters.variables.forEach(::environment)

        if (parameters.paths.isNotEmpty()) {
            val path = parameters.paths.fold(System.getenv(PATH)) { left, right -> "$right:$left" }
            environment(PATH, path)
        }

        val command = if (parameters.commands.isEmpty()) mainCommand else {
            parameters.commands.fold(mainCommand) { left, right ->
                right.toBuilder().then(left).build()
            }
        }

        commandLine(line = command.interpret(parameters.interpreter))
    }

    override fun execute(
        command: Command,
        workingDirectory: File?
    ): ExecResult = execOperations.exec {
        configure(
            mainCommand = command,
            workingDirectory = workingDirectory
        )
    }

    override fun executeWithOutput(
        command: Command,
        workingDirectory: File?
    ): String = execOperations.execOutput {
        configure(
            mainCommand = command,
            workingDirectory = workingDirectory
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // Factory
    ///////////////////////////////////////////////////////////////////////////

    class Factory(private val parameters: Parameters) : CommandExecutor.Factory {

        override fun create(execOperations: ExecOperations): CommandExecutor {
            return DefaultCommandExecutor(execOperations, parameters)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    data class Parameters(
        val interpreter: CommandInterpreter,
        val commands: List<Command>,
        val paths: List<String>,
        val variables: Map<String, String>
    ) : Serializable

    ///////////////////////////////////////////////////////////////////////////
    // Companion
    ///////////////////////////////////////////////////////////////////////////

    private companion object {

        const val PATH = "PATH"
    }
}