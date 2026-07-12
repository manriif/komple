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