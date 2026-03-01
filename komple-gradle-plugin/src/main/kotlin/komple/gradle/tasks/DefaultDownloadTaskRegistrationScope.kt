package komple.gradle.tasks

import de.undercouch.gradle.tasks.download.Download
import komple.gradle.integrity.DefaultIntegrityCheckScope
import komple.gradle.kompleToolsDownloadsDirectory
import komple.integrity.IntegrityCheckScope
import komple.integrity.IntegrityChecker
import komple.tasks.DownloadTaskRegistrationScope
import komple.tasks.register
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [DownloadTaskRegistrationScope].
 */
internal class DefaultDownloadTaskRegistrationScope(
    project: Project,
    toolName: String
) : DownloadTaskRegistrationScope,
    DefaultTaskRegistrationScope(project, toolName) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(Directory) -> Unit
    ): TaskProvider<T> {
        // Unique directory per tool
        val downloadDirectory = project.gradle.kompleToolsDownloadsDirectory.dir(toolName)

        return project.registerToolTask(taskName("Download"), klass) {
            description = "Download $toolName"
            configure(this, downloadDirectory)
        }
    }

    override fun registerDefault(
        url: Provider<String>,
        verify: IntegrityCheckScope.() -> IntegrityChecker
    ): TaskProvider<*> = register<Download> { downloadDirectory ->
        val fileExtension = url.map { it.substringAfterLast('.') }

        val fileName = fileExtension.map { extension ->
            "${toolName}${extension.takeIf { it.isNotEmpty() }?.let { ".$it" }.orEmpty()}"
        }

        val destination = downloadDirectory.file(fileName)

        onlyIf { url.isPresent }
        inputs.property("url", url)

        dest(destination)
        src(url)
        overwrite(false)
        quiet(false)

        val integrityChecker = verify(DefaultIntegrityCheckScope(project))

        doLast {
            integrityChecker.check(destination.get().asFile)
        }
    }
}