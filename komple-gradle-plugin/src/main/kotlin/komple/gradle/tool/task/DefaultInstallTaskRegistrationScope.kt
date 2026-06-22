package komple.gradle.tool.task

import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.InstallTaskContext
import komple.tool.task.InstallTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [InstallTaskRegistrationScope].
 */
internal class DefaultInstallTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>,
    private val extractTask: TaskProvider<*>
) : InstallTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension, InstallTaskContext>(context) {

    override val taskPostfix: String
        get() = TASK_TOOL_INSTALL_POSTFIX

    override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(context: InstallTaskContext) -> Unit
    ): TaskProvider<T> = registerToolTask(klass, cacheable) { tracker ->
        description = "Install the $toolName tool."

        configure(
            DefaultInstallTaskContext(
                tracker = tracker,
                outputDirectory = project.gradle.kompleToolsInstallsDirectory.dir(toolNameCompat),
                inputDirectory = extractTask.outputDirectory(project.layout),
                execEnvironmentProvider = context.execEnvironmentProvider
            )
        )
    }

    override fun skip(): TaskProvider<*> = extractTask
}