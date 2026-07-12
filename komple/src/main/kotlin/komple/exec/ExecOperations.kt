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

import org.gradle.process.ExecOperations
import org.gradle.process.ExecSpec
import java.io.ByteArrayOutputStream

///////////////////////////////////////////////////////////////////////////
// Regular exec
///////////////////////////////////////////////////////////////////////////

/**
 * Sets the full command line, including the executable to be executed plus its arguments.
 */
@IgnorableReturnValue
public fun ExecSpec.command(line: CommandLine): ExecSpec = commandLine(*line.args)

///////////////////////////////////////////////////////////////////////////
// Exec with output
///////////////////////////////////////////////////////////////////////////

/**
 * Executes a command configured with [action] and returns the output.
 */
public fun ExecOperations.execOutput(action: ExecSpec.() -> Unit): String {
    return ByteArrayOutputStream().use { output ->
        exec {
            standardOutput = output
            action()
        }

        output.toString(Charsets.UTF_8).trimEnd()
    }
}

/**
 * Executes a single line command given [args] and returns the output.
 */
public fun ExecOperations.execOutput(vararg args: Any): String {
    return execOutput {
        commandLine(*args)
    }
}

/**
 * Executes a single line command given [args] and returns the output.
 */
public fun ExecOperations.execOutput(args: Iterable<Any>): String {
    return execOutput {
        commandLine(args)
    }
}

/**
 * Executes [line] and returns the output.
 */
public fun ExecOperations.execOutput(line: CommandLine): String {
    return execOutput {
        command(line = line)
    }
}