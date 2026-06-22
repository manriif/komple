package komple.exec

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.process.ExecOperations

/**
 * Holds execution context.
 */
public interface ExecEnvironment {

    /**
     * Commands that should be executed before the main command.
     */
    @get:Internal
    public val commands: ListProperty<Command>

    /**
     * Paths to executables.
     */
    @get:Input
    public val paths: ListProperty<String>

    /**
     * Environment variables.
     */
    @get:Input
    public val variables: MapProperty<String, String>

    /**
     * Returns a new [CommandExecutor.Factory].
     */
    public fun createCommandExecutorFactory(): CommandExecutor.Factory
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a new [CommandExecutor] wrapping [execOperations].
 */
public fun ExecEnvironment.createCommandExecutor(execOperations: ExecOperations): CommandExecutor =
    createCommandExecutorFactory().create(execOperations)