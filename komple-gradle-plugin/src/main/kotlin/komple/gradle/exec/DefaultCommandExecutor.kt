package komple.gradle.exec

import komple.exec.Command
import komple.exec.CommandExecutor
import komple.exec.CommandInterpreter
import komple.exec.ExecEnvironment
import komple.exec.commandLine
import komple.exec.execOutput
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File
import javax.inject.Inject

/**
 * Default implementation of [CommandExecutor].
 */
public abstract class DefaultCommandExecutor @Inject internal constructor() : CommandExecutor {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    /**
     * The [CommandInterpreter] to use for executing commands.
     */
    public abstract val commandInterpreter: Property<CommandInterpreter>

    /**
     * The environments used to populate the process.
     */
    public abstract val execEnvironments: ListProperty<ExecEnvironment>

    /**
     * Configures the execution environment and sets the final command line.
     */
    private fun ExecSpec.configure(
        mainCommand: Command,
        workingDirectory: File?
    ) {
        workingDirectory?.let { workingDir = it }

        val envs = execEnvironments.get()

        if (envs.isEmpty()) {
            commandLine(line = mainCommand.interpret(commandInterpreter.get()))
            return
        }

        val commands = mutableListOf<Command>()
        val paths = mutableListOf<String>()

        envs.forEach { env ->
            commands.addAll(env.commands.get())
            paths.addAll(env.paths.get())

            env.variables.get()
                .forEach(::environment)
        }

        if (paths.isNotEmpty()) {
            val path = paths.fold(System.getenv(PATH)) { left, right -> "$right:$left" }
            environment(PATH, path)
        }

        val command = if (commands.isEmpty()) mainCommand else {
            commands.fold(mainCommand) { left, right ->
                right.toBuilder().then(left).build()
            }
        }

        commandLine(line = command.interpret(commandInterpreter.get()))
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
    // Companion
    ///////////////////////////////////////////////////////////////////////////

    private companion object {

        const val PATH = "PATH"
    }
}