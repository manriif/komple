package komple.gradle.tool.task

import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.task.TASK_TOOL_INSTALL_POSTFIX
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.Inputs
import komple.tool.task.InstallTaskContext
import komple.tool.task.InstallTaskRegistrationScope
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
    private val extractInputs: Inputs
) : InstallTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension>(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: InstallTaskContext) -> Unit
    ): TaskProvider<T> = registerTask(TASK_TOOL_INSTALL_POSTFIX, klass) { outputChanged ->
        description = "Install $toolName"

        val installDirectory = project.gradle.kompleToolsInstallsDirectory.dir(toolName)

        val installContext = DefaultInstallTaskContext(
            outputDirectory = installDirectory,
            outputChanged = outputChanged,
            inputs = extractInputs
        )

        inputs.files(extractInputs.files)
        configure(this, installContext)

        check(!outputs.files.isEmpty) {
            "Install task did not registered outputs"
        }

        check(outputs.files.files.size == 1) {
            "Install task must output a single directory only"
        }

        val fileOperations = project.serviceOf<FileSystemOperations>()

        doFirst {
            fileOperations.delete {
                delete(extractInputs.files)
                delete(installDirectory)
            }
        }
    }

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_INSTALL_POSTFIX)
}