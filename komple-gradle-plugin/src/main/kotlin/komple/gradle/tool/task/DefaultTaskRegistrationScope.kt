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
import komple.tool.task.TaskContext
import komple.tool.task.TaskRegistrationScope
import komple.tool.task.doFirstWhenOutputChanged
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
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
     * Invokes [configurator] and ensures no output file(s) were registered for task.
     */
    protected inline fun <T : Task, C> T.configureTask(
        context: C,
        outputDirectory: Provider<Directory>,
        configurator: T.(C) -> Unit
    ) {
        configurator(this, context)

        check(outputs.files.isEmpty) {
            "Task must not register output file(s)"
        }

        outputs.dir(outputDirectory)
    }

    /**
     * Invokes [configurator] and ensures no output file(s) were registered for task.
     */
    protected inline fun <T : Task, C : TaskContext> T.configureTask(
        context: C,
        configurator: T.(C) -> Unit,
        deleteFirst: Boolean
    ) {
        configureTask(
            context = context,
            outputDirectory = project.providers.provider(context::outputDirectory),
            configurator = configurator
        )

        if (deleteFirst) {
            val fileOperations = project.serviceOf<FileSystemOperations>()

            context.doFirstWhenOutputChanged {
                fileOperations.delete {
                    delete(context.outputDirectory)
                }
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