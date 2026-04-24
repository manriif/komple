package komple.gradle.tool.task

import komple.exec.ExecService
import komple.gradle.kompleToolsExtractsDirectory
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.ExtractTaskContext
import komple.tool.task.ExtractTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [ExtractTaskRegistrationScope].
 */
internal class DefaultExtractTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>,
    private val execServiceProvider: Provider<ExecService>,
    private val integrityTask: TaskProvider<*>
) : ExtractTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension>(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: ExtractTaskContext) -> Unit
    ): TaskProvider<T> = registerTask(TASK_TOOL_EXTRACT_POSTFIX, klass) { outputChanged ->
        description = "Extract $toolName"

        val extractContext = DefaultExtractTaskContext(
            downloadDirectory = integrityTask.outputDir(project.layout),
            execServiceProvider = execServiceProvider,
            outputDirectory = project.gradle.kompleToolsExtractsDirectory.dir(toolName),
            outputChanged = outputChanged
        )

        inputs.dir(extractContext.downloadDirectory.directory)

        configureTask(
            context = extractContext,
            configurator = configure,
            deleteFirst = true
        )
    }

    override fun skip(): TaskProvider<*> {
        return integrityTask
    }

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_EXTRACT_POSTFIX)
}