package komple.gradle.tasks

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

private const val KOMPLE_TOOLS_TASK_GROUP = "komple tools"

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