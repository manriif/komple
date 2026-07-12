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
import org.gradle.process.ExecResult
import java.io.File
import java.io.Serializable

/**
 * Gives access to command execution in an environment where registered tools are available.
 */
public interface CommandExecutor {

    /**
     * Executes the [command], in an environment configured by registered tools.
     */
    @IgnorableReturnValue
    public fun execute(
        command: Command,
        workingDirectory: File? = null
    ): ExecResult

    /**
     * Executes the [command], in an environment configured by registered tools and returns the
     * output.
     */
    @IgnorableReturnValue
    public fun executeWithOutput(
        command: Command,
        workingDirectory: File? = null
    ): String

    ///////////////////////////////////////////////////////////////////////////
    // Factory
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Factory for instantiating [CommandExecutor].
     */
    public interface Factory : Serializable {

        /**
         * Creates a new instance of [CommandExecutor] wrapping [execOperations].
         */
        public fun create(execOperations: ExecOperations): CommandExecutor
    }
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Executes the command built from [args], in an environment configured by registered tools.
 */
@IgnorableReturnValue
public fun CommandExecutor.execute(
    vararg args: Any,
    workingDirectory: File? = null
): ExecResult {
    return execute(Command(*args), workingDirectory)
}

/**
 * Executes the command built from [args], in an environment configured by registered tools and
 * returns the output.
 */
@IgnorableReturnValue
public fun CommandExecutor.executeWithOutput(
    vararg args: Any,
    workingDirectory: File? = null
): String {
    return executeWithOutput(Command(*args), workingDirectory)
}