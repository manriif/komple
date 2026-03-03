package komple.gradle.tool.install

import komple.gradle.kompleToolsDownloadsDirectory
import komple.gradle.task.TASK_TOOL_DOWNLOAD_POSTFIX
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.install.DownloadContext
import komple.tool.install.DownloadTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [DownloadTaskRegistrationScope].
 */
internal class DefaultDownloadTaskRegistrationScope(context: KompleToolConfigContext) :
    DownloadTaskRegistrationScope,
    DefaultTaskRegistrationScope(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(DownloadContext) -> Unit
    ): TaskProvider<T> {
        val downloadDirectory = project.gradle.kompleToolsDownloadsDirectory.dir(toolName)
        val downloadContext = DefaultDownloadContext(downloadDirectory)

        return registerTask(TASK_TOOL_DOWNLOAD_POSTFIX, klass) {
            description = "Download $toolName"
            configure(this, downloadContext)
        }
    }
}