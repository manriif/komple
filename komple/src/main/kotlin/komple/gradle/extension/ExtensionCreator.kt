package komple.gradle.extension

import komple.KompleInternalApi
import kotlin.reflect.KClass

/**
 * Extends Gradle extension API for Komple.
 */
public interface ExtensionCreator {

    /**
     * Creates an extension of type [Extension] with [name].
     * Constructor [args] are passed to the instance factory.
     * The returned extension itself can be configured inside [configure].
     */
    public fun <Extension : Any> createExtension(
        name: String,
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionContext<Extension>.() -> Unit)? = null
    ): Extension

    /**
     * Retrieve the extension of type [Extension] previously created with [createExtension].
     */
    @KompleInternalApi
    public fun <Extension : Any> retrieveExtension(type: KClass<Extension>): Extension
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Creates an extension of type [Extension] with [name].
 * Constructor [args] are passed to the instance factory.
 * The returned extension itself can be configured inside [configure].
 */
public inline fun <reified Extension : Any> ExtensionCreator.createExtension(
    name: String,
    vararg args: Any,
    noinline configure: (ExtensionContext<Extension>.() -> Unit)? = null
): Extension {
    return createExtension(
        name = name,
        type = Extension::class,
        args = args,
        configure = configure
    )
}


