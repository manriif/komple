package komple.gradle.task

import komple.gradle.kompleToolsDownloadsDirectory
import komple.gradle.tool.KompleToolConfigContext
import komple.task.DownloadContext
import komple.task.DownloadTaskRegistrationScope
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

        return project.registerToolTask(toolTaskName("Download"), klass) {
            description = "Download $toolName"
            configure(this, downloadContext)
        }
    }
}