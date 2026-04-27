package komple.gradle.tool

import komple.exec.CommandExecutor
import komple.exec.ExecEnvironment
import komple.gradle.deps.DependencyGraph
import komple.tool.KompleTool
import komple.tool.configurator.KompleToolConfigurator
import komple.tool.extension.KompleToolExtension
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Default implementation of [KompleTool].
 */
internal class DefaultKompleTool<Extension : KompleToolExtension>(
    val configurator: KompleToolConfigurator<Extension>,
    val extension: Extension,
    val toolName: String,
    val dependencyGraph: DependencyGraph<RootKompleTool>,
    val execEnvironments: MutableList<ExecEnvironment>,
    val commandExecutor: Provider<CommandExecutor>,
    override val execEnvironment: ExecEnvironment,
    override val installTaskProvider: TaskProvider<*>,
    override val installDirectory: Provider<Directory>
) : RootKompleTool {

    override fun getName(): String {
        return toolName
    }

    override fun dependsOn(other: RootKompleTool) {
        dependencyGraph.addDependency(this, other)
    }

    override fun toString(): String {
        return toolName
    }
}