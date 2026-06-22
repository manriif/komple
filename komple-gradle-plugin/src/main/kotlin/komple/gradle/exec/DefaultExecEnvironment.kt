package komple.gradle.exec

import komple.exec.CommandExecutor
import komple.exec.CommandInterpreter
import komple.exec.ExtendableExecEnvironment
import org.gradle.api.Named
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import javax.inject.Inject

/**
 * Default implementation of [ExtendableExecEnvironment].
 */
internal abstract class DefaultExecEnvironment
@Inject constructor(private val environmentName: String) :
    ExtendableExecEnvironment,
    Named {

    /**
     * Interpreter that interpret tool commands.
     */
    @get:Internal
    abstract val commandInterpreter: Property<CommandInterpreter>

    @Internal
    override fun getName(): String {
        return environmentName
    }

    override fun createCommandExecutorFactory(): CommandExecutor.Factory {
        val environments = shellEnvironments.get().toMutableList().apply {
            add(this@DefaultExecEnvironment)
        }

        val parameters = DefaultCommandExecutor.Parameters(
            interpreter = commandInterpreter.get(),
            commands = environments.flatMap { it.commands.get() },
            paths = environments.flatMap { it.paths.get() },
            variables = environments
                .flatMap { it.variables.get().entries }
                .associateBy({ it.key }, { it.value })
        )

        return DefaultCommandExecutor.Factory(parameters)
    }
}