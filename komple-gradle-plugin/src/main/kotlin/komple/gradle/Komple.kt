package komple.gradle

import komple.extension.ExtensionScope
import komple.gradle.extension.ExtensionContextImpl
import komple.gradle.extension.extensions
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import kotlin.reflect.KClass

internal class Komple(
    val extension: KompleExtension,
    val project: Project
) {

    private val registeredExtensionNames = mutableSetOf<String>()
    private val registeredExtensions = mutableMapOf<KClass<*>, Any>()

    fun <Extension : Any> create(
        name: String,
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionScope<Extension>.() -> Unit)?
    ): Extension {
        check(registeredExtensionNames.add(name)) {
            "An extension with name $name is already registered under the Komple extension"
        }

        return extension.extensions.create(name, type, *args).also { extension ->
            registeredExtensions[type] = extension
            configure?.invoke(ExtensionContextImpl(extension, project))
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <Extension : Any> retrieve(type: KClass<Extension>): Extension {
        return checkNotNull(registeredExtensions[type]) {
            "No extension registered for type $type"
        } as Extension
    }
}