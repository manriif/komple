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
    DefaultTaskRegistrationScope<Extension>(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(context: InstallTaskContext) -> Unit
    ): TaskProvider<T> = registerTask(TASK_TOOL_INSTALL_POSTFIX, klass, cacheable) { context ->
        description = "Install the $toolName tool."

        val installContext = DefaultInstallTaskContext(
            context = context,
            extractDirectory = extractTask.outputDir(project.layout),
            execEnvironment = this@DefaultInstallTaskRegistrationScope.context.execEnvironment,
            outputDirectory = project.gradle.kompleToolsInstallsDirectory.dir(toolNameCompat)
        )

        configureTask(
            context = installContext,
            configurator = configure,
            deleteFirst = true
        )

        if (inputs.files.isEmpty) {
            logger.warn("Task $name did not registered an input directory, using extract one")
            inputs.dir(installContext.extractDirectory.directory)
        }
    }

    override fun skip(): TaskProvider<*> = extractTask

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_INSTALL_POSTFIX)
}