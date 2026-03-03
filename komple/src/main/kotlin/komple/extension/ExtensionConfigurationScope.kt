package komple.extension

import org.gradle.api.Project
import kotlin.reflect.KClass

/**
 * Extends Gradle extension API for Komple.
 */
public interface ExtensionConfigurationScope {

    /**
     * The Gradle [Project] associated with the Komple plugin.
     */
    public val project: Project

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


