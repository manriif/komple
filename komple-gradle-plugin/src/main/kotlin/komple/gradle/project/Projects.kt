package komple.gradle.project

import komple.gradle.util.camelCased
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

private const val KOMPLE_PROJECTS_TASK_GROUP = "komple projects"

///////////////////////////////////////////////////////////////////////////
// Task
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a directory where to store project generated files.
 */
internal fun Project.projectGeneratedOutputDir(
    projectName: String,
    subdirectory: String
): Provider<Directory> {
    return layout.buildDirectory.dir(
        "generated/komple/projects/${projectName}/$subdirectory"
    )
}

/**
 * Returns a conventional name for a task and for the project.
 */
internal fun projectTaskName(projectName: String, postfix: String): String {
    return "${projectName.camelCased()}$postfix"
}

/**
 * Registers a project task and applies default configuration.
 */
internal fun <T : Task> Project.registerProjectTask(
    name: String,
    type: KClass<T>,
    configure: T.() -> Unit
): TaskProvider<T> = tasks.register(name, type.java) {
    group = KOMPLE_PROJECTS_TASK_GROUP
    configure(this)
}