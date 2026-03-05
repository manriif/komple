package komple.exec

/**
 * Command interpreter.
 */
public fun interface CommandInterpreter {

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

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Builds a [CommandLine] interpreted by `this` [CommandInterpreter].
 */
public operator fun CommandInterpreter.invoke(vararg args: Any): CommandLine {
    return CommandBuilder(*args)
        .build()
        .interpret(this)
}

/**
 * Builds a [CommandLine] interpreted by `this` [CommandInterpreter].
 */
public inline operator fun CommandInterpreter.invoke(
    vararg args: Any,
    block: CommandBuilder.() -> Unit
): CommandLine {
    return CommandBuilder(*args)
        .apply(block)
        .build()
        .interpret(this)
}