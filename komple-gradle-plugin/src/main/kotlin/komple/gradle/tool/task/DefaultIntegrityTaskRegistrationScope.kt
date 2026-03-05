package komple.gradle.tool.task

import komple.gradle.task.TASK_TOOL_INTEGRITY_POSTFIX
import komple.gradle.task.registerToolTask
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.Inputs
import komple.tool.task.IntegrityTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [IntegrityTaskRegistrationScope].
 */
internal class DefaultIntegrityTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>,
    private val downloadInputs: Inputs
) : IntegrityTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension>(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(inputs: Inputs) -> Unit
    ): TaskProvider<T> = context.project.registerToolTask(
        name = toolTaskName(TASK_TOOL_INTEGRITY_POSTFIX),
        type = klass
    ) {
        description = "Check $toolName integrity"

        inputs.files(downloadInputs.files)
        configure(this, downloadInputs)

        check(outputs.files.isEmpty) {
            "Integrity task should not declare output file(s)"
        }

        outputs.files(downloadInputs.files)
    }

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_INTEGRITY_POSTFIX)
}