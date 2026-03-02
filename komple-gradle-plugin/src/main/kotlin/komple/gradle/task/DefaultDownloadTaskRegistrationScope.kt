package komple.gradle.task

import komple.gradle.Komple
import komple.gradle.kompleToolsDownloadsDirectory
import komple.task.DownloadContext
import komple.task.DownloadTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [DownloadTaskRegistrationScope].
 */
internal class DefaultDownloadTaskRegistrationScope(
    komple: Komple,
    toolName: String,
) : DownloadTaskRegistrationScope,
    DefaultTaskRegistrationScope(komple, toolName) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(DownloadContext) -> Unit
    ): TaskProvider<T> {
        val downloadDirectory = komple.project.gradle.kompleToolsDownloadsDirectory.dir(toolName)
        val downloadContext = DefaultDownloadContext(downloadDirectory)

        return komple.project.registerToolTask(toolTaskName("Download"), klass) {
            description = "Download $toolName"
            configure(this, downloadContext)
        }
    }
}