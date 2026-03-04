package komple.gradle.tool.install

import komple.gradle.kompleToolsExtractsDirectory
import komple.gradle.task.TASK_TOOL_EXTRACT_POSTFIX
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.install.ExtractTaskContext
import komple.tool.install.ExtractTaskRegistrationScope
import komple.tool.install.Inputs
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
        configure: T.(context: ExtractTaskContext) -> Unit
    ): TaskProvider<T> = registerTask(TASK_TOOL_EXTRACT_POSTFIX, klass) { outputChanged ->
        description = "Extract $toolName"

        val extractDirectory = project.gradle.kompleToolsExtractsDirectory.dir(toolName)
        val extractContext = DefaultExtractTaskContext(
            outputDirectory = extractDirectory,
            outputChanged = outputChanged,
            inputs = integrityInputs
        )

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