package komple.gradle.extension

import komple.gradle.util.ClosableScope
import komple.tool.extension.ExtensionScope
import org.gradle.api.Project
import kotlin.reflect.KProperty1

/**
 * Implementation of [ExtensionScope].
 */
internal class DefaultExtensionScope<Extension : Any>(
    override val extension: Extension,
    override val project: Project
) : ExtensionScope<Extension>,
    ClosableScope() {

    override fun <Child : Any> add(
        property: KProperty1<Extension, Child>,
        configure: (ExtensionScope<Child>.() -> Unit)?
    ): Child = notClosed {
        property.get(extension).also { child ->
            extension.extensions.add(property.name, child)
            DefaultExtensionScope(project, child, configure)
        }
    }

    companion object {

        /**
         * Invokes [configure] with a [DefaultExtensionScope] instance if [configure] is not `null`.
         */
        operator fun <Extension : Any> invoke(
            project: Project,
            extension: Extension,
            configure: (ExtensionScope<Extension>.() -> Unit)?
        ) {
            configure?.let { action ->
                DefaultExtensionScope(extension, project).use(action)
            }
        }
    }
}