package komple.gradle.tool.task

import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.task.TASK_TOOL_INSTALL_POSTFIX
import komple.gradle.task.outputFiles
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.InstallTaskContext
import komple.tool.task.InstallTaskRegistrationScope
import komple.tool.task.doFirstWhenOutputChanged
import komple.tool.task.doLastWhenOutputChanged
import org.gradle.api.Task
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import kotlin.reflect.KClass

/**
 * Default implementation of [InstallTaskRegistrationScope].
 */
internal class DefaultInstallTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>,
    private val extractTask: TaskProvider<*>
) : InstallTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension>(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: InstallTaskContext) -> Unit
    ): TaskProvider<T> = registerTask(TASK_TOOL_INSTALL_POSTFIX, klass) { outputChanged ->
        description = "Install $toolName"

        val installContext = DefaultInstallTaskContext(
            outputDirectory = project.gradle.kompleToolsInstallsDirectory.dir(toolName),
            outputChanged = outputChanged,
            inputs = extractTask.outputFiles(project.layout)
        )

        inputs.files(installContext.inputs.files)
        configure(this, installContext)

        val outputFiles = outputs.files

        check(outputFiles.count() == 1) {
            "Install task must output a single directory"
        }

        val fileOperations = project.serviceOf<FileSystemOperations>()
        val outputFile = outputFiles.singleFile

        installContext.doFirstWhenOutputChanged {
            fileOperations.delete {
                delete(outputFile.listFiles())
            }
        }

        installContext.doLastWhenOutputChanged {
            check(outputFile.isDirectory) {
                "Install task output must be a directory"
            }
        }
    }

    override fun skipInstallation(): TaskProvider<*> {
        return extractTask
    }

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_INSTALL_POSTFIX)
}