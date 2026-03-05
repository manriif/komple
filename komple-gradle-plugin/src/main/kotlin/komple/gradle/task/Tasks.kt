package komple.gradle.task

import komple.gradle.kompleChecksumsDirectory
import komple.gradle.tool.task.DefaultInputs
import komple.tool.task.Inputs
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import java.io.File
import java.util.Locale.getDefault
import kotlin.reflect.KClass

private const val KOMPLE_TOOLS_TASK_GROUP = "komple tools"
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

///////////////////////////////////////////////////////////////////////////
// Output
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a provider resolving the output files of the task.
 */
internal fun Provider<out Task>.outputFiles(): Provider<FileCollection> {
    return map { it.outputs.files }
}

/**
 * Returns the outputs files as [Inputs] object.
 */
internal fun Provider<out Task>.outputFiles(layout: ProjectLayout): Inputs = DefaultInputs(
    files = outputFiles(),
    layout = layout
)

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