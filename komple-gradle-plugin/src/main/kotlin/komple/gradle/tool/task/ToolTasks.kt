package komple.gradle.tool.task

import komple.gradle.task.registerKompleTask
import komple.gradle.util.camelCased
import komple.task.TaskContext
import komple.tool.task.TaskDirectory
import org.gradle.api.Task
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

internal const val KOMPLE_TOOLS_TASK_GROUP = "komple tools"
internal const val TASK_TOOL_DOWNLOAD_POSTFIX = "Download"
internal const val TASK_TOOL_INTEGRITY_POSTFIX = "Integrity"
internal const val TASK_TOOL_EXTRACT_POSTFIX = "Extract"
internal const val TASK_TOOL_INSTALL_POSTFIX = "Install"

///////////////////////////////////////////////////////////////////////////
// Registration
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a conventional name for a task and for the tool.
 */
internal fun toolTaskName(toolName: String, postfix: String): String {
    return "${toolName.camelCased()}$postfix"
}

/**
 * Registers tool task and applies default configuration.
 */
internal fun <T : Task> TaskContainer.registerToolTask(
    name: String,
    type: KClass<T>,
    cacheable: Boolean,
    configure: T.(context: TaskContext) -> Unit
): TaskProvider<T> = registerKompleTask(name, type, cacheable) { context ->
    group = KOMPLE_TOOLS_TASK_GROUP
    configure(this, context)
}

///////////////////////////////////////////////////////////////////////////
// Output
///////////////////////////////////////////////////////////////////////////

/**
 * Returns the outputs directory as [TaskDirectory] object.
 */
internal fun Provider<out Task>.outputDir(layout: ProjectLayout): TaskDirectory =
    DefaultTaskDirectory(layout.dir(map { it.outputs.files.singleFile }))