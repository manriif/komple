package komple.gradle.tool.task

import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.task.checksumFile
import komple.gradle.task.checksumsEquals
import komple.gradle.task.registerToolTask
import komple.gradle.task.toolTaskName
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.gradle.util.sha256
import komple.platform.Host
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import komple.tool.task.TaskRegistrationScope
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import java.io.File
import kotlin.reflect.KClass

/**
 * Default base implementation for [TaskRegistrationScope].
 */
internal abstract class DefaultTaskRegistrationScope<Extension : KompleToolExtension>(
    protected val context: KompleToolConfigContext<Extension>
) : TaskRegistrationScope<Extension>,
    HasExtension<Extension> by context,
    ClosableScope() {

    override val host: Host
        get() = notClosed { CurrentHost }

    override val providers: ProviderFactory
        get() = notClosed { context.project.providers }

    override val toolName: String
        get() = context.toolName

    /**
     * Returns a conventional name for a task and for the tool.
     */
    protected fun toolTaskName(postfix: String): String {
        return toolTaskName(toolName, postfix)
    }

    /**
     * Registers a task [T] and returns a provider to the registered task.
     */
    protected inline fun <T : Task> registerTask(
        postfix: String,
        type: KClass<T>,
        crossinline configure: T.(outputChanged: Provider<Boolean>) -> Unit
    ): TaskProvider<T> = context.project.registerToolTask(toolTaskName(postfix), type) {
        val checksumFile = project.checksumFile(name)
        val checksumInputs = project.objects.fileCollection()

        val checksumProvider = project.provider {
            checksumInputs.files.filter(File::exists).sha256()
        }

        configure(checksumProvider.map { checksum ->
            !checksumsEquals(checksumFile.asFile, checksum)
        })

        checksumInputs.from(outputs.files)

        doLast {
            checksumFile.asFile.run {
                parentFile.mkdirs()
                writeText(checksumProvider.get())
            }
        }
    }

    /**
     * Registers a task that always fails when executed.
     */
    protected fun registerUnsupportedTask(postfix: String): TaskProvider<*> {
        return context.project.registerToolTask(toolTaskName(postfix), DefaultTask::class) {
            doLast {
                throw UnsupportedHostException("Host is not supported")
            }
        }
    }
}