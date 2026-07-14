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

import komple.exec.CommandExecutor
import komple.exec.CommandInterpreter
import komple.exec.ExtendableExecEnvironment
import org.gradle.api.Named
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import javax.inject.Inject

/**
 * Default implementation of [ExtendableExecEnvironment].
 */
internal abstract class DefaultExecEnvironment
@Inject constructor(private val environmentName: String) :
    ExtendableExecEnvironment,
    Named {

    /**
     * Interpreter that interpret tool commands.
     */
    @get:Internal
    abstract val commandInterpreter: Property<CommandInterpreter>

    @Internal
    override fun getName(): String {
        return environmentName
    }

    override fun createCommandExecutorFactory(): CommandExecutor.Factory {
        val environments = shellEnvironments.get().toMutableList().apply {
            add(this@DefaultExecEnvironment)
        }

        val parameters = DefaultCommandExecutor.Parameters(
            interpreter = commandInterpreter.get(),
            commands = environments.flatMap { it.commands.get() },
            paths = environments.flatMap { it.paths.get() },
            variables = environments
                .flatMap { it.variables.get().entries }
                .associateBy({ it.key }, { it.value })
        )

        return DefaultCommandExecutor.Factory(parameters)
    }
}