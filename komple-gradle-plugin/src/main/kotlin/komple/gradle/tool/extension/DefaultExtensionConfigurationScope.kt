package komple.gradle.tool.extension

import komple.gradle.extension.KompleRootProjectExtension
import komple.gradle.extension.extensions
import komple.gradle.util.ClosableScope
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.ExtensionScope
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import kotlin.reflect.KClass

/**
 * Default implementation of [ExtensionConfigurationScope].
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
            name = toolName,
            type = type,
            constructionArguments = args
        ).also { extension ->
            configure?.let { action ->
                ExtensionScopeImpl(extension).use(action)
            }
        }
    }

    /**
     * Default implementation of [ExtensionScope].
     */
    internal inner class ExtensionScopeImpl(override val extension: Extension) :
        ExtensionScope<Extension>,
        ClosableScope() {

        override val project: Project
            get() = notClosed { this@DefaultExtensionConfigurationScope.project }
    }
}