package komple.tool.extension

import kotlin.reflect.KClass

/**
 * Extends Komple extension API by creating a tool specific extension.
 */
public interface ExtensionConfigurationScope<Extension : KompleToolExtension> {

    /**
     * Creates an extension of type [Extension].
     * Arguments [args] are passed to [Extension] constructor.
     * The returned extension itself can be configured inside [configure].
     */
    public fun createExtension(
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionScope<Extension>.() -> Unit)? = null
    ): Extension
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Creates an extension of type [Extension].
 * Arguments [args] are passed to [Extension] constructor.
 * The returned extension itself can be configured inside [configure].
 */
public inline fun <reified Extension : KompleToolExtension>
        ExtensionConfigurationScope<Extension>.createExtension(
    vararg args: Any,
    noinline configure: (ExtensionScope<Extension>.() -> Unit)? = null
): Extension = createExtension(
    type = Extension::class,
    args = args,
    configure = configure
)


