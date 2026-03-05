package komple.exec

import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider

/**
 * Environment for command execution.
 * The tools contribute to the environment by register
 */
public interface ExecEnvironmentBuilder {

    /**
     * Registers a command line that will be executed first.
     */
    public fun commandLine(commandProvider: Provider<Command>)

    /**
     * Adds [pathProvider] to the environment path variable.
     */
    public fun path(pathProvider: Provider<String>)

    /**
     * Defines [valueProvider] as environment variable for [name].
     */
    public fun variable(
        name: String,
        valueProvider: Provider<String>
    )
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Adds [pathProvider] resolved absolute path to the environment path variable.
 */
public fun ExecEnvironmentBuilder.path(pathProvider: Provider<out FileSystemLocation>) {
    path(pathProvider.map { it.asFile.absolutePath })
}

/**
 * Defines [valueProvider] resolved absolute path as environment variable for [name].
 */
public fun ExecEnvironmentBuilder.variable(
    name: String,
    valueProvider: Provider<out FileSystemLocation>
) {
    variable(
        name = name,
        valueProvider = valueProvider.map { it.asFile.absolutePath }
    )
}