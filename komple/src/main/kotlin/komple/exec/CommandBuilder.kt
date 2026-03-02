package komple.exec

/**
 * Fluent builder for composing shell-style command chains.
 *
 * Operator mapping:
 *  - pipe        → `|`
 *  - pipeAll     → `|&`
 *  - then        → `&&`
 *  - otherwise   → `||`
 *
 * Arguments are accepted as `Any` and are converted to strings by the implementation when
 * constructing the command line.
 */
public interface CommandBuilder {

    /**
     * `|` — pipe stdout to the given command.
     */
    @IgnorableReturnValue
    public fun pipe(vararg args: Any): CommandBuilder

    /**
     * `|` — pipe stdout to the given command.
     */
    @IgnorableReturnValue
    public fun pipe(args: Iterable<Any>): CommandBuilder

    /**
     * `|&` — pipe stdout and stderr to the given command.
     */
    @IgnorableReturnValue
    public fun pipeAll(vararg args: Any): CommandBuilder

    /**
     * `|&` — pipe stdout and stderr to the given command.
     */
    @IgnorableReturnValue
    public fun pipeAll(args: Iterable<Any>): CommandBuilder

    /**
     * `&&` — execute only if the previous command succeeds.
     */
    @IgnorableReturnValue
    public fun then(vararg args: Any): CommandBuilder

    /**
     * `&&` — execute only if the previous command succeeds.
     */
    @IgnorableReturnValue
    public fun then(args: Iterable<Any>): CommandBuilder

    /**
     * `||` — execute only if the previous command fails.
     */
    @IgnorableReturnValue
    public fun otherwise(vararg args: Any): CommandBuilder

    /**
     * `||` — execute only if the previous command fails.
     */
    @IgnorableReturnValue
    public fun otherwise(args: Iterable<Any>): CommandBuilder
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a shell command initialized with [commandArgs].
 * The command can be further edited inside [build].
 */
public fun shellCommand(
    shellArgs: Array<String>,
    vararg commandArgs: Any,
    build: (CommandBuilder.() -> Unit)? = null
): Command {
    val builder = ShellCommandBuilder(
        shellArgs = shellArgs,
        command = commandArgs
    )

    build?.invoke(builder)
    return builder.build()
}

/**
 * Returns a Bash command initialized with [args].
 * The command can be further edited inside [build].
 */
public fun bash(
    vararg args: Any,
    build: (CommandBuilder.() -> Unit)? = null
): Command = shellCommand(
    shellArgs = arrayOf("bash", "-c"),
    commandArgs = args,
    build = build
)