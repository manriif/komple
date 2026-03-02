package komple.gradle.task

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import java.util.Locale.getDefault
import kotlin.reflect.KClass

private const val KOMPLE_TOOLS_TASK_GROUP = "komple tools"

/**
 * Returns a conventional name for a task and for the tool.
 */
internal fun toolTaskName(toolName: String, postfix: String): String {
    val prefix = toolName.replaceFirstChar { it.lowercase(getDefault()) }
    return "$prefix$postfix"
}

/**
 * Registers tool task and applies default configuration.
 */
internal fun <T : Task> Project.registerToolTask(
    name: String,
    type: KClass<T>,
    configure: T.() -> Unit
): TaskProvider<T> = tasks.register(name, type.java) {
    group = KOMPLE_TOOLS_TASK_GROUP
    configure(this)
}

/**
 * Returns a provider resolving the output files of the task.
 */
internal fun Provider<out Task>.outputFiles(): Provider<FileCollection> {
    return map { it.outputs.files }
}

/**
 * Returns a provider resolving the input files of the task.
 */
internal fun Provider<out Task>.inputFiles(): Provider<FileCollection> {
    return map { it.inputs.files }
}