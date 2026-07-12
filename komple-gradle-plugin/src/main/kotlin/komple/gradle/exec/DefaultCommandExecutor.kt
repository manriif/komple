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
package komple.gradle.exec

import komple.exec.Command
import komple.exec.CommandExecutor
import komple.exec.CommandInterpreter
import komple.exec.command
import komple.exec.execOutput
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File
import java.io.Serializable

/**
 * Default implementation of [CommandExecutor].
 */
internal class DefaultCommandExecutor(
    private val execOperations: ExecOperations,
    private val parameters: Parameters
) : CommandExecutor {

    /**
     * Configures the execution environment and sets the final command line.
     */
    private fun ExecSpec.configure(
        mainCommand: Command,
        workingDirectory: File?
    ) {
        workingDirectory?.let { workingDir = it }

        parameters.variables.forEach(::environment)

        if (parameters.paths.isNotEmpty()) {
            val path = parameters.paths.fold(System.getenv(PATH)) { left, right -> "$right:$left" }
            environment(PATH, path)
        }

        val command = if (parameters.commands.isEmpty()) mainCommand else {
            parameters.commands.fold(mainCommand) { left, right ->
                right.toBuilder().then(left).build()
            }
        }

        command(line = command.interpret(parameters.interpreter))
    }

    override fun execute(
        command: Command,
        workingDirectory: File?
    ): ExecResult = execOperations.exec {
        configure(
            mainCommand = command,
            workingDirectory = workingDirectory
        )
    }

    override fun executeWithOutput(
        command: Command,
        workingDirectory: File?
    ): String = execOperations.execOutput {
        configure(
            mainCommand = command,
            workingDirectory = workingDirectory
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // Factory
    ///////////////////////////////////////////////////////////////////////////

    class Factory(private val parameters: Parameters) : CommandExecutor.Factory {

        override fun create(execOperations: ExecOperations): CommandExecutor {
            return DefaultCommandExecutor(execOperations, parameters)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    data class Parameters(
        val interpreter: CommandInterpreter,
        val commands: List<Command>,
        val paths: List<String>,
        val variables: Map<String, String>
    ) : Serializable

    ///////////////////////////////////////////////////////////////////////////
    // Companion
    ///////////////////////////////////////////////////////////////////////////

    private companion object {

        const val PATH = "PATH"
    }
}