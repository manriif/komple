package komple.gradle.exec

import komple.exec.Command
import komple.exec.ExecEnvironmentBuilder
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty

/**
 * Default implementation of [ExecEnvironmentBuilder]
 */
internal abstract class ExecEnvironment {

    /**
     * Paths to executables.
     */
    abstract val paths: ListProperty<String>

    /**
     * Environment variables.
     */
    abstract val variables: MapProperty<String, String>

    /**
     * Commands that should be executed before main command.
     */
    abstract val commands: ListProperty<Command>
}