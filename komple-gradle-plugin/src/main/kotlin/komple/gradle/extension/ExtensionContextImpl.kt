package komple.gradle.extension

import org.gradle.kotlin.dsl.create
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * Implementation of [ExtensionContext].
 */
internal class ExtensionContextImpl<Extension : Any>(
    override val extension: Extension
) : ExtensionContext<Extension> {

    override fun <Extension : Any> createExtension(
        name: String,
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionContext<Extension>.() -> Unit)?
    ): Extension {
        return extension.extensions.create(name, type, *args).also { extension ->
            configure?.invoke(ExtensionContextImpl(extension))
        }
    }

    override fun <Child : Any> add(
        property: KProperty1<Extension, Child>,
        configure: (ExtensionContext<Child>.() -> Unit)?
    ): Child {
        return property.get(extension).also { child ->
            extension.extensions.add(property.name, child)
            configure?.invoke(ExtensionContextImpl(child))
        }
    }
}