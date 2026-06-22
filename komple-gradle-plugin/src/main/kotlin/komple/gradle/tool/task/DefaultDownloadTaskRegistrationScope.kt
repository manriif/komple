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
    DefaultTaskRegistrationScope<Extension, DownloadTaskContext>(context) {

    override val taskPostfix: String
        get() = TASK_TOOL_DOWNLOAD_POSTFIX

    override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(DownloadTaskContext) -> Unit
    ): TaskProvider<T> = registerToolTask(klass, cacheable) { tracker ->
        description = "Download the $toolName tool."

        configure(
            DefaultDownloadTaskContext(
                tracker = tracker,
                outputDirectory = project.gradle.kompleToolsDownloadsDirectory.dir(toolNameCompat),
                execEnvironmentProvider = context.execEnvironmentProvider
            )
        )
    }

    override fun skip(): TaskProvider<*> =
        registerFailureTask(UnsupportedOperationException("No file to download for tool $toolName"))
}