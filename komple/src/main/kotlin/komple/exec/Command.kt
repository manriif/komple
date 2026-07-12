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
public fun Command(vararg args: Any): Command =
    CommandBuilder(*args).build()

/**
 * Builds and returns a command with [args].
 */
public inline fun Command(
    vararg args: Any,
    build: CommandBuilder.() -> Unit
): Command = CommandBuilder(*args).apply(build).build()