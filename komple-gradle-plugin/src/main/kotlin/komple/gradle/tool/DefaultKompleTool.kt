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
    override val installTaskProvider: TaskProvider<*>,
    override val installDirectory: Provider<Directory>
) : RootKompleTool {

    private val usageExecEnvironment by lazy(::createUsageExecEnvironment)
    val usageExecEnvironmentProvider = ExecEnvironmentProvider(::usageExecEnvironment)

    override fun getName(): String {
        return toolName
    }

    override fun dependsOn(other: RootKompleTool) {
        dependencyGraph.addDependency(this, other)
    }

    override fun toString(): String {
        return toolName
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
}