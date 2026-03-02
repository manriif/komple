package komple.gradle.task

import komple.gradle.kompleToolsExtractsDirectory
import komple.gradle.tool.KompleToolConfigContext
import komple.task.ExtractContext
import komple.task.ExtractTaskRegistrationScope
import komple.task.Inputs
import org.gradle.api.Task
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import kotlin.reflect.KClass

/**
 * Default implementation of [ExtractTaskRegistrationScope].
 */
internal class DefaultExtractTaskRegistrationScope(
    context: KompleToolConfigContext,
    private val integrityInputs: Inputs
) : ExtractTaskRegistrationScope,
    DefaultTaskRegistrationScope(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: ExtractContext) -> Unit
    ): TaskProvider<T> {
        val extractDirectory = project.gradle.kompleToolsExtractsDirectory.dir(toolName)
        val extractContext = DefaultExtractContext(extractDirectory, integrityInputs)

        return project.registerToolTask(toolTaskName("Extract"), klass) {
            description = "Extract $toolName"

            inputs.files(integrityInputs.files)
            configure(this, extractContext)

            check(!outputs.files.isEmpty) {
                "Extract task did not registered outputs"
            }

            val fileOperations = project.serviceOf<FileSystemOperations>()

            doFirst {
                fileOperations.delete {
                    delete(integrityInputs.files)
                    delete(extractDirectory)
                }
            }
        }
    }
}