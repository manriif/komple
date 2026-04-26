package komple.gradle.tool.task

import komple.gradle.kompleToolsDownloadsDirectory
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
        description = "Download the $toolName tool."

        val downloadContext = DefaultDownloadTaskContext(
            outputDirectory = project.gradle.kompleToolsDownloadsDirectory.dir(toolNameCompat),
            outputChanged = outputChanged
        )

        configureTask(
            context = downloadContext,
            configurator = configure,
            deleteFirst = false
        )
    }

    override fun skip(): TaskProvider<*> {
        val toolName = toolName

        return registerFailureTask(TASK_TOOL_DOWNLOAD_POSTFIX) {
            throw UnsupportedOperationException("No file to download for tool $toolName")
        }
    }

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_DOWNLOAD_POSTFIX)
}