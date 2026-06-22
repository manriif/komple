package komple.gradle.tool.task

import komple.gradle.task.registerKompleTask
import komple.gradle.util.camelCased
import komple.task.TaskStateTracker
import org.gradle.api.Task
import org.gradle.api.file.Directory
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
internal fun toolTaskName(toolName: String, postfix: String): String =
    "${toolName.camelCased()}$postfix"

/**
 * Registers tool task and applies default configuration.
 */
internal fun <T : Task> TaskContainer.registerToolTask(
    name: String,
    type: KClass<T>,
    cacheable: Boolean,
    configure: T.(context: TaskStateTracker) -> Unit
): TaskProvider<T> = registerKompleTask(name, type, cacheable) { context ->
    group = KOMPLE_TOOLS_TASK_GROUP
    configure(this, context)
}

///////////////////////////////////////////////////////////////////////////
// Output
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a [Provider] to the output file of the task as a single directory.
 */
internal fun Provider<out Task>.outputDirectory(layout: ProjectLayout): Provider<Directory> =
    layout.dir(map { it.outputs.files.singleFile })