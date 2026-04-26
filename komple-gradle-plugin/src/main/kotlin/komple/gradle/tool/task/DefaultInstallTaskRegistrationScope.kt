package komple.gradle.tool.task

import komple.exec.CommandExecutor
import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.InstallTaskContext
import komple.tool.task.InstallTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [InstallTaskRegistrationScope].
 */
internal class DefaultInstallTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>,
    private val commandExecutor: Provider<CommandExecutor>,
    private val extractTask: TaskProvider<*>
) : InstallTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension>(context) {

    override fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: InstallTaskContext) -> Unit
    ): TaskProvider<T> = registerTask(TASK_TOOL_INSTALL_POSTFIX, klass) { outputChanged ->
        description = "Install the $toolName tool."

        val installContext = DefaultInstallTaskContext(
            extractDirectory = extractTask.outputDir(project.layout),
            commandExecutor = commandExecutor,
            outputDirectory = project.gradle.kompleToolsInstallsDirectory.dir(toolNameCompat),
            outputChanged = outputChanged
        )

        inputs.dir(installContext.extractDirectory.directory)

        configureTask(
            context = installContext,
            configurator = configure,
            deleteFirst = true
        )
    }

    override fun skip(): TaskProvider<*> {
        return extractTask
    }

    override fun unsupported(): TaskProvider<*> =
        registerUnsupportedTask(TASK_TOOL_INSTALL_POSTFIX)
}