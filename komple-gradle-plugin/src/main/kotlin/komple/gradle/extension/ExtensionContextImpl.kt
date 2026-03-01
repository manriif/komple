package komple.gradle.extension

import komple.extension.ExtensionScope
import org.gradle.kotlin.dsl.create
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * Implementation of [komple.extension.ExtensionScope].
 */
internal class ExtensionContextImpl<Extension : Any>(
    override val extension: Extension
) : ExtensionScope<Extension> {

    override fun <Extension : Any> createExtension(
        name: String,
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionScope<Extension>.() -> Unit)?
    ): Extension {
        return extension.extensions.create(name, type, *args).also { extension ->
            configure?.invoke(ExtensionContextImpl(extension))
        }
    }

    override fun <Child : Any> add(
        property: KProperty1<Extension, Child>,
        configure: (ExtensionScope<Child>.() -> Unit)?
    ): Child {
        return property.get(extension).also { child ->
            extension.extensions.add(property.name, child)
            configure?.invoke(ExtensionContextImpl(child))
        }
    }
}