package komple.gradle.task

import komple.gradle.Komple
import komple.task.Inputs
import komple.task.IntegrityTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [IntegrityTaskRegistrationScope].
 */
internal class DefaultIntegrityTaskRegistrationScope(
    komple: Komple,
    toolName: String,
    private val downloadInputs: Inputs
) : IntegrityTaskRegistrationScope,
    DefaultTaskRegistrationScope(komple, toolName) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(inputs: Inputs) -> Unit
    ): TaskProvider<T> {
        return komple.project.registerToolTask(toolTaskName("Integrity"), klass) {
            description = "Check $toolName integrity"

            inputs.files(downloadInputs.files)
            configure(this, downloadInputs)

            check(outputs.files.isEmpty) {
                "Integrity task should not register outputs"
            }

            outputs.files(downloadInputs.files)
        }
    }
}