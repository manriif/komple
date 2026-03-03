package komple.exec

import org.gradle.api.provider.Provider

/**
 * Environment for command execution.
 * The tools contribute to the environment by register
 */
public interface ExecEnvironmentBuilder {

    /**
     * Adds [pathProvider] to the environment.
     */
    public fun path(pathProvider: Provider<String>)

    /**
     * Defines [valueProvider] as environment variable for [name].
     */
    public fun variable(
        name: String,
        valueProvider: Provider<String>
    )

    /**
     * Registers a command line that will be executed first.
     */
    public fun commandLine(commandProvider: Provider<CommandLine>)
}