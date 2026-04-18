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
     */
    private fun ExecSpec.configure(
        mainCommand: Command,
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

        val command = if (commands.isEmpty()) mainCommand else {
            commands.fold(mainCommand) { left, right ->
                right.toBuilder().then(left).build()
            }
        }

        commandLine(line = command.interpret(parameters.interpreter.get()))
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

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    interface Params : BuildServiceParameters {

        val interpreter: Property<CommandInterpreter>
        val environment: Property<ExecEnvironment>
    }

    ///////////////////////////////////////////////////////////////////////////
    // Companion
    ///////////////////////////////////////////////////////////////////////////

    private companion object {

        const val PATH = "PATH"
    }
}