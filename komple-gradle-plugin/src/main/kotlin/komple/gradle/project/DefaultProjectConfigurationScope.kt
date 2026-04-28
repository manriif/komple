package komple.gradle.project

import komple.exec.CommandExecutor
import komple.gradle.extension.DefaultExtensionScope
import komple.gradle.platform.CurrentHost
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.gradle.util.dashCased
import komple.gradle.util.pascalCased
import komple.platform.Host
import komple.project.ProjectConfigurationScope
import komple.project.ProjectConfigurator
import komple.tool.extension.ExtensionScope
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import kotlin.reflect.KClass

/**
 * Default implementation of [ProjectConfigurationScope].
 */
internal class DefaultProjectConfigurationScope<Extension : KompleToolExtension>(
    private val context: KompleToolConfigContext<Extension>,
    private val projectExtension: KompleProjectExtension,
    override val configurator: ProjectConfigurator,
    override val installDirectory: Provider<Directory>,
    override val commandExecutor: Provider<CommandExecutor>
) : ProjectConfigurationScope<Extension>,
    HasExtension<Extension> by context,
    ClosableScope() {

    override val host: Host
        get() = notClosed { CurrentHost }

    override fun generatedDirectory(): Provider<Directory> {
        return context.project.layout.projectGeneratedOutputDir(
            projectName = configurator.project.name,
            subdirectory = context.toolName.dashCased()
        )
    }

    override fun <E : Any> createExtension(
        name: String,
        type: KClass<E>,
        vararg args: Any,
        configure: (ExtensionScope<E>.() -> Unit)?
    ): E = notClosed {
        projectExtension.extensions.create(
            name = name,
            type = type,
            constructionArguments = args
        ).also { extension ->
            DefaultExtensionScope(context.project, extension, configure)
        }
    }

    override fun <T : Task> registerTask(
        postfix: String,
        type: KClass<T>,
        configure: (T.() -> Unit)?
    ): TaskProvider<T> {
        val name = projectTaskName(
            projectName = configurator.project.name,
            postfix = "${context.toolName}${postfix.pascalCased()}"
        )

        return context.project.tasks.registerProjectTask(name, type) {
            configure?.invoke(this)
        }
    }
}