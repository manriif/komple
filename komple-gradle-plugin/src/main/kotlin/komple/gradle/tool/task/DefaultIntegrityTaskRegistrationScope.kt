package komple.gradle.tool.task

import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.IntegrityTaskContext
import komple.tool.task.IntegrityTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [IntegrityTaskRegistrationScope].
 */
internal class DefaultIntegrityTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>,
    private val downloadTask: TaskProvider<*>
) : IntegrityTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension, IntegrityTaskContext>(context) {

    override val taskPostfix: String
        get() = TASK_TOOL_INTEGRITY_POSTFIX

    override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(context: IntegrityTaskContext) -> Unit
    ): TaskProvider<T> = registerToolTask(klass, cacheable) {
        description = "Check the $toolName tool's integrity."
        val downloadDirectory = downloadTask.outputDirectory(project.layout)

        configure(DefaultIntegrityTaskContext(
            inputDirectory = downloadDirectory,
            execEnvironmentProvider = context.execEnvironmentProvider
        ))

        check(outputs.files.isEmpty) {
            "Integrity task must not register outputs file(s)"
        }

        outputs.dir(downloadDirectory)
    }

    override fun skip(): TaskProvider<*> = downloadTask
}