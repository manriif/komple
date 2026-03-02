package komple.gradle.task

import komple.gradle.Komple
import komple.gradle.kompleToolsExtractsDirectory
import komple.task.Inputs
import komple.task.ExtractContext
import komple.task.ExtractTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import kotlin.reflect.KClass

/**
 * Default implementation of [ExtractTaskRegistrationScope].
 */
internal class DefaultExtractTaskRegistrationScope(
    komple: Komple,
    toolName: String,
    private val integrityInputs: Inputs
) : ExtractTaskRegistrationScope,
    DefaultTaskRegistrationScope(komple, toolName) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: ExtractContext) -> Unit
    ): TaskProvider<T> {
        val extractDirectory = komple.project.gradle.kompleToolsExtractsDirectory.dir(toolName)
        val extractContext = DefaultExtractContext(extractDirectory, integrityInputs)

        return komple.project.registerToolTask(toolTaskName("Extract"), klass) {
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