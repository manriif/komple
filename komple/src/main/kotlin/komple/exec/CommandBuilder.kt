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
     * `|` — pipe stdout to the given command.
     */
    @IgnorableReturnValue
    public fun pipe(command: Command): CommandBuilder

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
     * `|&` — pipe stdout and stderr to the given command.
     */
    @IgnorableReturnValue
    public fun pipeAll(command: Command): CommandBuilder

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
     * `&&` — execute only if the previous command succeeds.
     */
    @IgnorableReturnValue
    public fun then(command: Command): CommandBuilder

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

    /**
     * `||` — execute only if the previous command fails.
     */
    @IgnorableReturnValue
    public fun otherwise(command: Command): CommandBuilder

    /**
     * Build the command.
     */
    public fun build(): Command
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a new [CommandBuilder] initialized with [args].
 */
public fun CommandBuilder(vararg args: Any): CommandBuilder {
    return ShellCommandBuilder(args)
}