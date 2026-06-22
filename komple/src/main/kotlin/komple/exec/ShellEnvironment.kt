package komple.exec

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

/**
 * Holds components for populating a shell.
 */
public interface ShellEnvironment {

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
}