package komple.gradle.tool.task

import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.IntegrityTaskRegistrationScope
import komple.tool.task.TaskDirectory
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
    DefaultTaskRegistrationScope<Extension>(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(directory: TaskDirectory) -> Unit
    ): TaskProvider<T> = context.project.registerToolTask(
        name = toolTaskName(TASK_TOOL_INTEGRITY_POSTFIX),
        type = klass
    ) {
        description = "Check $toolName integrity"

        val downloadDirectory = downloadTask.outputDir(project.layout)
        inputs.dir(downloadDirectory.directory)

        configureTask(
            context = downloadDirectory,
            outputDirectory = downloadDirectory.directory,
            configurator = configure
        )
    }

    override fun skip(): TaskProvider<*> {
        return downloadTask
    }

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_INTEGRITY_POSTFIX)
}