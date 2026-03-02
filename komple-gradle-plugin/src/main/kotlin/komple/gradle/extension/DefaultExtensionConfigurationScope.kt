package komple.gradle.extension

import komple.extension.ExtensionConfigurationScope
import komple.extension.ExtensionScope
import komple.gradle.Komple
import komple.gradle.util.ClosableScope
import org.gradle.api.Project
import kotlin.reflect.KClass

/**
 * Default implementation of [ExtensionConfigurationScope].
 */
internal class DefaultExtensionConfigurationScope(
    private val komple: Komple
) : ExtensionConfigurationScope,
    ClosableScope() {

    override val project: Project
        get() = notClosed { komple.project }

    override fun <Extension : Any> createExtension(
        name: String,
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionScope<Extension>.() -> Unit)?
    ): Extension = notClosed {
        komple.create(
            name = name,
            type = type,
            args = args,
            configure = configure
        )
    }
}