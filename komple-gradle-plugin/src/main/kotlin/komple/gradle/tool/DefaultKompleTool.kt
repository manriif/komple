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
package komple.gradle.tool

import komple.exec.ExecEnvironment
import komple.exec.ShellEnvironment
import komple.exec.addEnvironment
import komple.exec.plusAssign
import komple.gradle.deps.DependencyGraph
import komple.gradle.exec.DefaultExecEnvironment
import komple.gradle.exec.ExecEnvironmentProvider
import komple.tool.KompleTool
import komple.tool.configurator.KompleToolConfigurator
import komple.tool.extension.KompleToolExtension
import org.gradle.api.file.Directory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.newInstance

/**
 * Default implementation of [KompleTool].
 */
internal class DefaultKompleTool<Extension : KompleToolExtension>(
    private val objects: ObjectFactory,
    val configurator: KompleToolConfigurator<Extension>,
    val extension: Extension,
    val toolName: String,
    private val dependencyGraph: DependencyGraph<RootKompleTool>,
    private val installExecEnvironment: DefaultExecEnvironment,
    override val shellEnvironment: ShellEnvironment,
    private val allTaskProviders: List<TaskProvider<*>>,
    override val installTaskProvider: TaskProvider<*>,
    override val installDirectory: Provider<Directory>
) : RootKompleTool {

    private val usageExecEnvironment by lazy(::createUsageExecEnvironment)
    val usageExecEnvironmentProvider = ExecEnvironmentProvider(::usageExecEnvironment)

    override fun getName(): String {
        return toolName
    }

    override fun disableInstallationTasks() {
        allTaskProviders.forEach { provider ->
            provider.configure { enabled = false }
        }
    }

    override fun dependsOn(other: RootKompleTool) {
        dependencyGraph.addDependency(this, other)
    }

    /**
     * Adds [environment] to [installExecEnvironment].
     */
    fun addEnvironment(environment: ShellEnvironment) {
        installExecEnvironment.addEnvironment(environment)
    }

    /**
     * Returns a new [ExecEnvironment] that also includes the tool own [shellEnvironment].
     *
     * The [installExecEnvironment] only contains [ShellEnvironment]s from other tools as including
     * the [shellEnvironment] to it may lead to circular task dependency due to [installDirectory]
     * being resolved from the installation task.
     */
    private fun createUsageExecEnvironment(): ExecEnvironment {
        val name = "${installExecEnvironment.name}_Usage"
        val execEnvironment = objects.newInstance<DefaultExecEnvironment>(name)

        execEnvironment.commandInterpreter = installExecEnvironment.commandInterpreter
        execEnvironment += installExecEnvironment
        execEnvironment += shellEnvironment

        return execEnvironment
    }

    override fun toString(): String = toolName
}