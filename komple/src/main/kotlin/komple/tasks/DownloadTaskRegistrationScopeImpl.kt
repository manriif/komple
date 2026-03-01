package komple.tasks

import de.undercouch.gradle.tasks.download.Download
import komple.gradle.kompleToolsDownloadsDirectory
import komple.integrity.DefaultIntegrityCheckScope
import komple.integrity.IntegrityCheck
import komple.integrity.IntegrityCheckScope
import komple.platform.Host
import komple.tool.KompleTool
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import kotlin.reflect.KClass

/**
 * Default implementation of [DownloadTaskRegistrationScope].
 */
internal class DefaultDownloadTaskRegistrationScope(
    private val project: Project,
    private val tool: KompleTool,
    private val displayName: String?,
    override val host: Host
) : DownloadTaskRegistrationScope {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(Directory) -> Unit
    ): Provider<RegularFile> {
        // Unique directory per tool
        val downloadDirectory = project.gradle.kompleToolsDownloadsDirectory.dir(tool.name)

        val taskProvider = project.registerToolTask("${tool.name}Download", klass) {
            description = "Download the tool ${displayName ?: tool.name}"
            configure(this, downloadDirectory)
        }

        return project.layout.file(taskProvider.map { it.outputs.files.singleFile })
    }

    override fun registerDefault(
        url: Provider<String>,
        verify: IntegrityCheckScope.() -> IntegrityCheck
    ): Provider<RegularFile> = register<Download> { downloadDirectory ->
        val fileExtension = url.map { it.substringAfterLast('.') }

        val fileName = fileExtension.map { extension ->
            "${tool.name}${extension.takeIf { it.isNotEmpty() }?.let { ".$it" }.orEmpty()}"
        }

        val destination = downloadDirectory.file(fileName)

        onlyIf { url.isPresent }
        inputs.property("url", url)

        dest(destination)
        src(url)
        overwrite(false)
        quiet(false)

        val integrityCheck = verify(DefaultIntegrityCheckScope(project))

        doLast {
            integrityCheck.checker.check(destination.get().asFile)
        }
    }
}