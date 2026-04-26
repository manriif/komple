package komple.exec

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty

/**
 * Holds execution context.
 */
public interface ExecEnvironment {

    /**
     * Commands that should be executed before the main command.
     */
    public val commands: ListProperty<Command>

    /**
     * Paths to executables.
     */
    public val paths: ListProperty<String>

    /**
     * Environment variables.
     */
    public val variables: MapProperty<String, String>
}