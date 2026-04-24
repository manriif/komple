package komple.exec

import java.io.Serializable

/**
 * Represents a command that can be mutated using [toBuilder].
 */
public interface Command : Serializable {

    /**
     * Returns `this` command interpreted by [interpreter].
     */
    public fun interpret(interpreter: CommandInterpreter): CommandLine

    /**
     * Returns a [CommandBuilder] initialized with `this` command arguments.
     */
    public fun toBuilder(): CommandBuilder
}

///////////////////////////////////////////////////////////////////////////
// Factory
///////////////////////////////////////////////////////////////////////////

/**
 * Builds and returns a command with [args].
 */
public fun Command(vararg args: Any): Command {
    return CommandBuilder(*args).build()
}

/**
 * Builds and returns a command with [args].
 */
public inline fun Command(
    vararg args: Any,
    build: CommandBuilder.() -> Unit
): Command {
    return CommandBuilder(*args).apply(build).build()
}