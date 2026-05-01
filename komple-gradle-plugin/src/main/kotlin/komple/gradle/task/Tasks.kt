package komple.gradle.task

import komple.gradle.kompleChecksumsDirectory
import komple.gradle.util.dashCased
import komple.gradle.util.sha256
import komple.task.TaskContext
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import java.io.File
import kotlin.reflect.KClass

/**
 * Configures this task and invokes [configure] with a [TaskContext].
 *
 * The file changes detection for [TaskContext.outputChanged] applies on all the task's output
 * files.
 *
 * TODO SHA-256 is too slow on large file collection, replace with a faster solution
 */
public fun <T: Task> T.configureWithContext(configure: T.(context: TaskContext) -> Unit) {
    val checksumFile = project.checksumFile(name.dashCased())
    val checksumInputs = project.objects.fileCollection()

    val checksumProvider = project.provider {
        checksumInputs.files.filter(File::exists).sha256()
    }

    val context = DefaultTaskContext(checksumProvider.map { checksum ->
        !checksumsEquals(checksumFile.asFile, checksum)
    })

    configure(this, context)
    checksumInputs.from(outputs.files)

    if (!outputs.files.isEmpty) {
        doLast {
            checksumFile.asFile.run {
                parentFile.mkdirs()
                val freshChecksum = checksumInputs.files.filter(File::exists).sha256()

                if (!checksumsEquals(checksumFile.asFile, freshChecksum)) {
                    writeText(freshChecksum)
                }
            }
        }
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

///////////////////////////////////////////////////////////////////////////
// Checksum
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a file that can be used to store a checksum for task generated output(s).
 */
internal fun Project.checksumFile(taskName: String): RegularFile {
    var taskUniqueName = taskName
    var parentProject: Project? = this

    while (parentProject != null) {
        taskUniqueName = "${parentProject.name}-$taskUniqueName"
        parentProject = parentProject.parent
    }

    return gradle.kompleChecksumsDirectory.file(taskUniqueName.lowercase())
}

/**
 * Returns `true` if [checksumFile] exists and its value is equals to [checksum].
 */
internal fun checksumsEquals(
    checksumFile: File,
    checksum: String,
): Boolean {
    if (checksumFile.exists()) {
        val previous = checksumFile.readText()

        if (checksum == previous) {
            return true
        }
    }

    return false
}