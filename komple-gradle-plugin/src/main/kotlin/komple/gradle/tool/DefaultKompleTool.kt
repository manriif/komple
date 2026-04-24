package komple.gradle.tool

import komple.gradle.tool.graph.ToolDependencyGraph
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
    val dependencyGraph: ToolDependencyGraph,
    override val installTaskProvider: TaskProvider<*>,
    override val installDirectory: Provider<Directory>
) : KompleTool {

    override fun getName(): String {
        return toolName
    }

    override fun dependsOn(other: KompleTool) {
        dependencyGraph.addDependency(this, other)
    }
}