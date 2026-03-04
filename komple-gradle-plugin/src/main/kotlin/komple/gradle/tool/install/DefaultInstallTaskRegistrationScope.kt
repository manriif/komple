package komple.gradle.tool.install

import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.task.TASK_TOOL_INSTALL_POSTFIX
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.install.Inputs
import komple.tool.install.InstallTaskContext
import komple.tool.install.InstallTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import kotlin.reflect.KClass

/**
 * Default implementation of [InstallTaskRegistrationScope].
 */
internal class DefaultInstallTaskRegistrationScope(
    context: KompleToolConfigContext,
    private val extractInputs: Inputs
) : InstallTaskRegistrationScope,
    DefaultTaskRegistrationScope(context) {

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
}