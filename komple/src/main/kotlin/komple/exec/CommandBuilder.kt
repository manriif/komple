/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
     * Adds [args] to the end of current arguments.
     */
    @IgnorableReturnValue
    public fun append(vararg args: Any): CommandBuilder

    /**
     * Adds [args] to the end of current arguments.
     */
    @IgnorableReturnValue
    public fun append(args: Iterable<Any>): CommandBuilder

    /**
     * Adds [command] to the end of current arguments.
     */
    @IgnorableReturnValue
    public fun append(command: Command): CommandBuilder

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
    check(args.isNotEmpty()) {
        "At least one argument is expected for a command"
    }

    return ShellCommandBuilder(args)
}