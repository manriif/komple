package komple.gradle.tool

import komple.gradle.extension.DefaultExtensionScope
import komple.gradle.extension.KompleRootProjectExtension
import komple.gradle.extension.extensions
import komple.gradle.util.ClosableScope
import komple.gradle.util.camelCased
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.ExtensionScope
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import kotlin.reflect.KClass

/**
 * Default implementation of [komple.tool.extension.ExtensionConfigurationScope].
 */
internal class DefaultExtensionConfigurationScope<Extension : KompleToolExtension>(
    private val project: Project,
    private val rootExtension: KompleRootProjectExtension,
    private val toolName: String,
) : ExtensionConfigurationScope<Extension>,
    ClosableScope() {

    override fun createExtension(
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionScope<Extension>.() -> Unit)?
    ): Extension = notClosed {
        rootExtension.extensions.create(
            name = toolName.camelCased(),
            type = type,
            constructionArguments = args
        ).also { extension ->
            DefaultExtensionScope(project, extension, configure)
        }
    }
}