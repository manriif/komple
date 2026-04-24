package komple.exec

import java.io.Serializable

/**
 * Command interpreter.
 */
public fun interface CommandInterpreter : Serializable {

    /**
     * Returns the ready to execute command line given [args].
     */
    public fun createCommandLine(args: Array<String>): CommandLine
}

///////////////////////////////////////////////////////////////////////////
// Interpreters
///////////////////////////////////////////////////////////////////////////

/**
 * Bash [Command] interpreter.
 */
public val Bash: CommandInterpreter = CommandInterpreter { args ->
    DefaultCommandLine(arrayOf("bash", "-c", args.joinToString(" ")))
}

/**
 * Zsh [Command] interpreter.
 */
public val Zsh: CommandInterpreter = CommandInterpreter { args ->
    DefaultCommandLine(arrayOf("zsh", "-c", args.joinToString(" ")))
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Builds a [CommandLine] interpreted by `this` [CommandInterpreter].
 */
public operator fun CommandInterpreter.invoke(vararg args: Any): CommandLine {
    return Command(*args).interpret(this)
}

/**
 * Builds a [CommandLine] interpreted by `this` [CommandInterpreter].
 */
public inline operator fun CommandInterpreter.invoke(
    vararg args: Any,
    block: CommandBuilder.() -> Unit
): CommandLine {
    return Command(*args, build = block).interpret(this)
}