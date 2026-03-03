package komple.gradle.tool.install

import komple.gradle.task.TASK_TOOL_INTEGRITY_POSTFIX
import komple.gradle.task.registerToolTask
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.install.Inputs
import komple.tool.install.IntegrityTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [IntegrityTaskRegistrationScope].
 */
internal class DefaultIntegrityTaskRegistrationScope(
    context: KompleToolConfigContext,
    private val downloadInputs: Inputs
) : IntegrityTaskRegistrationScope,
    DefaultTaskRegistrationScope(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(inputs: Inputs) -> Unit
    ): TaskProvider<T> = project.registerToolTask(
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
}