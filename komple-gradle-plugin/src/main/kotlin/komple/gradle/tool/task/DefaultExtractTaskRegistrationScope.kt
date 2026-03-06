package komple.gradle.tool.task

import komple.gradle.kompleToolsExtractsDirectory
import komple.gradle.task.TASK_TOOL_EXTRACT_POSTFIX
import komple.gradle.task.outputFiles
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.ExtractTaskContext
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.Inputs
import komple.tool.task.doFirstWhenOutputChanged
import org.gradle.api.Task
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import kotlin.reflect.KClass

/**
 * Default implementation of [ExtractTaskRegistrationScope].
 */
internal class DefaultExtractTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>,
    private val integrityTask: TaskProvider<*>
) : ExtractTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension>(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: ExtractTaskContext) -> Unit
    ): TaskProvider<T> = registerTask(TASK_TOOL_EXTRACT_POSTFIX, klass) { outputChanged ->
        description = "Extract $toolName"

        val extractContext = DefaultExtractTaskContext(
            outputDirectory = project.gradle.kompleToolsExtractsDirectory.dir(toolName),
            outputChanged = outputChanged,
            inputs = integrityTask.outputFiles(project.layout)
        )

        inputs.files(extractContext.inputs.files)
        configure(this, extractContext)

        check(!outputs.files.isEmpty) {
            "Extract task did not registered outputs"
        }

        val fileOperations = project.serviceOf<FileSystemOperations>()
        val outputFiles = outputs.files

        extractContext.doFirstWhenOutputChanged {
            fileOperations.delete {
                delete(outputFiles)
            }
        }
    }

    override fun skipExtraction(): TaskProvider<*> {
        return integrityTask
    }

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_EXTRACT_POSTFIX)
}