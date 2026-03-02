package komple.gradle.task

import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.tool.KompleToolConfigContext
import komple.task.Inputs
import komple.task.InstallContext
import komple.task.InstallTaskRegistrationScope
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
    private val taskName: String,
    private val extractInputs: Inputs
) : InstallTaskRegistrationScope,
    DefaultTaskRegistrationScope(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: InstallContext) -> Unit
    ): TaskProvider<T> {
        val installDirectory = project.gradle.kompleToolsInstallsDirectory.dir(toolName)
        val installContext = DefaultInstallContext(installDirectory, extractInputs)

        return project.registerToolTask(taskName, klass) {
            description = "Install $toolName"

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
}