package komple.gradle.project

import komple.gradle.util.camelCased
import komple.gradle.util.dashCased
import komple.gradle.util.pascalCased
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

private const val KOMPLE_PROJECTS_TASK_GROUP = "komple projects"

///////////////////////////////////////////////////////////////////////////
// Task
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a directory where to store project generated files.
 */
internal fun ProjectLayout.projectGeneratedOutputDir(
    projectName: String,
    subdirectory: String
): Provider<Directory> {
    return buildDirectory.dir("generated/komple/projects/${projectName.dashCased()}/$subdirectory")
}

/**
 * Returns a conventional name for a task and for the project.
 */
internal fun projectTaskName(projectName: String, postfix: String): String {
    return "${projectName.camelCased()}${postfix.pascalCased()}"
}

/**
 * Registers a project task and applies default configuration.
 */
internal fun <T : Task> TaskContainer.registerProjectTask(
    name: String,
    type: KClass<T>,
    configure: T.() -> Unit
): TaskProvider<T> = register(name, type.java) {
    group = KOMPLE_PROJECTS_TASK_GROUP
    outputs.cacheIf { true }
    configure(this)
}