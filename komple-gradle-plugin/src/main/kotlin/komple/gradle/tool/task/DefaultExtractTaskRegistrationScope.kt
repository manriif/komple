package komple.gradle.tool.task

import komple.gradle.kompleToolsExtractsDirectory
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.ExtractTaskContext
import komple.tool.task.ExtractTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [ExtractTaskRegistrationScope].
 */
internal class DefaultExtractTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>,
    private val integrityTask: TaskProvider<*>
) : ExtractTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension, ExtractTaskContext>(context) {

    override val taskPostfix: String
        get() = TASK_TOOL_EXTRACT_POSTFIX

    override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(context: ExtractTaskContext) -> Unit
    ): TaskProvider<T> = registerToolTask(klass, cacheable) { tracker ->
        description = "Extract the $toolName tool."

        configure(
            DefaultExtractTaskContext(
                tracker = tracker,
                outputDirectory = project.gradle.kompleToolsExtractsDirectory.dir(toolNameCompat),
                inputDirectory = integrityTask.outputDirectory(project.layout),
                execEnvironmentProvider = context.execEnvironmentProvider
            )
        )
    }

    override fun skip(): TaskProvider<*> = integrityTask
}