package komple.gradle.tool.install

import komple.gradle.platform.CurrentHost
import komple.gradle.task.checksumFile
import komple.gradle.task.checksumsEquals
import komple.gradle.task.registerToolTask
import komple.gradle.task.toolTaskName
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.gradle.util.sha256
import komple.platform.Host
import komple.tool.install.TaskRegistrationScope
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import java.io.File
import kotlin.io.writeText
import kotlin.reflect.KClass

/**
 * Default base implementation for [TaskRegistrationScope].
 */
internal abstract class DefaultTaskRegistrationScope(protected val context: KompleToolConfigContext) :
    TaskRegistrationScope,
    ClosableScope() {

    protected val project: Project
        inline get() = context.komple.project

    override val host: Host
        get() = notClosed { CurrentHost }

    override val providers: ProviderFactory
        get() = notClosed { context.komple.project.providers }

    override val toolName: String
        get() = context.toolName

    override fun <Extension : Any> extension(type: KClass<Extension>): Extension = notClosed {
        context.komple.retrieve(type)
    }

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
    ): TaskProvider<T> = project.registerToolTask(toolTaskName(postfix), type) {
        val checksumFile = project.checksumFile(name)
        val checksumInputs = project.objects.fileCollection()

        val checksumProvider = project.provider {
            checksumInputs.files.filter(File::exists).sha256()
        }

        configure(checksumProvider.map { checksum ->
            checksumsEquals(checksumFile.asFile, checksum)
        })

        checksumInputs.from(outputs.files)

        doLast task@{
            checksumFile.asFile.run {
                parentFile.mkdirs()
                writeText(checksumProvider.get())
            }
        }
    }
}