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
        cacheable: Boolean,
        configure: T.(directory: TaskDirectory) -> Unit
    ): TaskProvider<T> = context.project.tasks.registerToolTask(
        name = toolTaskName(TASK_TOOL_INTEGRITY_POSTFIX),
        type = klass,
        cacheable = cacheable,
    ) {
        description = "Check the $toolName tool's integrity."

        val downloadDirectory = downloadTask.outputDir(project.layout)

        configureTask(
            context = downloadDirectory,
            outputDirectory = downloadDirectory.directory,
            configurator = configure
        )

        if (inputs.files.isEmpty) {
            logger.warn("Task $name did not registered an input directory, using download one")
            inputs.dir(downloadDirectory.directory)
        }
    }

    override fun skip(): TaskProvider<*> = downloadTask

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_INTEGRITY_POSTFIX)
}