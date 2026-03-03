package komple.gradle.exec

import komple.exec.Command
import komple.exec.CommandInterpreter
import komple.exec.ExecService
import komple.exec.commandLine
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File
import javax.inject.Inject

/**
 * Service for command execution in the context of compilation.
 */
internal abstract class DefaultExecService @Inject constructor(
    private val execOperations: ExecOperations
) : BuildService<DefaultExecService.Params>,
    ExecService {

    /**
     * Configures the execution environment and sets the final command line.
     * TODO filter for command
     */
    private fun ExecSpec.configure(
        mainCommand: Command,
        interpreter: CommandInterpreter,
        workingDirectory: File?
    ) {
        workingDirectory?.let { workingDir = it }
        val execEnvironment = parameters.environment.get()

        execEnvironment.variables.orNull
            ?.forEach(::environment)

        execEnvironment.paths.orNull
            ?.takeUnless(List<String>::isEmpty)
            ?.fold(System.getenv(PATH)) { left, right -> "$right:$left" }
            ?.let { environment(PATH, it) }

        val commands = execEnvironment.commands.get()

        val command: Command = if (commands.isEmpty()) mainCommand else {
            commands.fold(mainCommand) { left, right ->
                right.toBuilder().then(left).build()
            }
        }

        commandLine(line = command.interpret(interpreter))
    }

    override fun exec(
        command: Command,
        interpreter: CommandInterpreter,
        workingDirectory: File?
    ): ExecResult = execOperations.exec {
        configure(
            mainCommand = command,
            interpreter = interpreter,
            workingDirectory = workingDirectory
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    interface Params : BuildServiceParameters {

        val environment: Property<ExecEnvironment>
    }

    ///////////////////////////////////////////////////////////////////////////
    // Companion
    ///////////////////////////////////////////////////////////////////////////

    private companion object {

        const val PATH = "PATH"
    }
}