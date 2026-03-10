package komple.gradle.tool.project

import komple.gradle.extension.KompleProjectExtension
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.gradle.util.camelCased
import komple.gradle.util.dashCased
import komple.gradle.util.pascalCased
import komple.project.KompleProject
import komple.tool.extension.ExtensionScope
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import komple.tool.project.ProjectConfigurationScope
import org.gradle.api.Project
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
    override val project: KompleProject
) : ProjectConfigurationScope<Extension>,
    HasExtension<Extension> by context,
    ClosableScope() {

    override fun generatedDirectory(subdirectory: String): Provider<Directory> {
        return context.project.layout.buildDirectory
            .dir("generated/komple/${project.name}/${context.toolName.dashCased()}/$subdirectory")
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
            configure?.let { action ->
                ExtensionScopeImpl(extension).use(action)
            }
        }
    }

    override fun <T : Task> registerTask(
        postfix: String,
        type: KClass<T>,
        configure: (T.() -> Unit)?
    ): TaskProvider<T> {
        val name = "${project.name}${context.toolName.pascalCased()}$postfix"

        return context.project.tasks.register(name, type.java) {
            configure?.invoke(this)
        }
    }

    /**
     * Implementation of [ExtensionScope].
     */
    internal inner class ExtensionScopeImpl<E : Any>(override val extension: E) :
        ExtensionScope<E>,
        ClosableScope() {

        override val project: Project
            get() = notClosed { context.project }
    }
}