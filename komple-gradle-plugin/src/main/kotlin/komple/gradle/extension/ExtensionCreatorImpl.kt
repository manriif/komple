package komple.gradle.extension

import org.gradle.kotlin.dsl.create
import kotlin.reflect.KClass

internal class ExtensionCreatorImpl : ExtensionCreator {

    private val registeredExtensionNames = mutableSetOf<String>()
    private val extensionsInstances = mutableMapOf<KClass<*>, Any>()

    override fun <Extension : Any> createExtension(
        name: String,
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionContext<Extension>.() -> Unit)?
    ): Extension {
        check(registeredExtensionNames.add(name)) {
            "An extension with name $name is already registered under the Komple extension"
        }

        return extensions.create(name, type, *args).also { extension ->
            extensionsInstances[type] = extension
            configure?.invoke(ExtensionContextImpl(extension))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Extension : Any> retrieveExtension(type: KClass<Extension>): Extension {
        return checkNotNull(extensionsInstances[type]) {
            "No extension registered as $type"
        } as Extension
    }
}