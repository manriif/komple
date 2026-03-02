package komple.extension

import komple.gradle.HasProject
import kotlin.reflect.KClass

/**
 * Extends Gradle extension API for Komple.
 */
public interface ExtensionConfigurationScope : HasProject {

    /**
     * Creates an extension of type [Extension] named after [name].
     * Constructor [args] are passed as arguments to [Extension] constructor.
     * The returned extension itself can be configured inside [configure].
     */
    public fun <Extension : Any> createExtension(
        name: String,
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionScope<Extension>.() -> Unit)? = null
    ): Extension
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Creates an extension of type [Extension] named after [name].
 * Constructor [args] are passed as arguments to [Extension] constructor.
 * The returned extension itself can be configured inside [configure].
 */
public inline fun <reified Extension : Any> ExtensionConfigurationScope.createExtension(
    name: String,
    vararg args: Any,
    noinline configure: (ExtensionScope<Extension>.() -> Unit)? = null
): Extension = createExtension(
    name = name,
    type = Extension::class,
    args = args,
    configure = configure
)


