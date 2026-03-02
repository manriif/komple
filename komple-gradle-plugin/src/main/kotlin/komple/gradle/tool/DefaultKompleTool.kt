package komple.gradle.tool

import komple.tool.KompleTool
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Default implementation of [KompleTool].
 */
internal class DefaultKompleTool(
    private val toolName: String,
    override val installTaskProvider: TaskProvider<*>,
    override val installDirectory: Provider<Directory>
) : KompleTool {

    override fun getName(): String {
        return toolName
    }
}