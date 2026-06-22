package komple.gradle.task

import komple.gradle.kompleChecksumsDirectory
import komple.gradle.util.dashCased
import komple.task.TaskContext
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Returns a file that can be used to store a checksum for task generated output(s).
 */
private fun Project.checksumFile(taskName: String): Directory {
    var taskUniqueName = taskName
    var parentProject: Project? = this

    while (parentProject != null) {
        taskUniqueName = "${parentProject.name}-$taskUniqueName"
        parentProject = parentProject.parent
    }

    return gradle.kompleChecksumsDirectory.dir(taskUniqueName.lowercase())
}

/**
 * Configures this task and invokes [configure] with a [TaskContext].
 */
public fun <T : Task> T.configureWithContext(configure: T.(context: TaskContext) -> Unit) {
    val checksumDirectory = project.checksumFile(name.dashCased())
    val context = DefaultTaskContext(checksumDirectory, logger, project.objects)

    configure(this, context)

    context.inputs = inputs
    context.outputFiles = outputs.files

    doLast {
        context.updateChecksums()
    }
}

/**
 * Registers tool task and applies default configuration.
 */
internal fun <T : Task> TaskContainer.registerKompleTask(
    name: String,
    type: KClass<T>,
    cacheable: Boolean,
    configure: T.(context: TaskContext) -> Unit,
): TaskProvider<T> = register(name, type.java) {
    if (cacheable) {
        outputs.cacheIf { true }
    }

    configureWithContext(configure)
}