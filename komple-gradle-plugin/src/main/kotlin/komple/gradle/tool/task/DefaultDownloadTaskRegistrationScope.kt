package komple.gradle.tool.task

import komple.gradle.kompleToolsDownloadsDirectory
import komple.gradle.task.TASK_TOOL_DOWNLOAD_POSTFIX
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.DownloadTaskContext
import komple.tool.task.DownloadTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [DownloadTaskRegistrationScope].
 */
internal class DefaultDownloadTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>
) : DownloadTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension>(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(DownloadTaskContext) -> Unit
    ): TaskProvider<T> = registerTask(TASK_TOOL_DOWNLOAD_POSTFIX, klass) { outputChanged ->
        description = "Download $toolName"

        val downloadContext = DefaultDownloadTaskContext(
            outputDirectory = project.gradle.kompleToolsDownloadsDirectory.dir(toolName),
            outputChanged = outputChanged
        )

        configureTask(
            context = downloadContext,
            configurator = configure,
            deleteFirst = false
        )
    }

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_DOWNLOAD_POSTFIX)
}