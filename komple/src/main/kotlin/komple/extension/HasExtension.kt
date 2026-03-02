package komple.extension

import kotlin.reflect.KClass

/**
 * Represents a Komple entity able to provide registered extension.
 */
public interface HasExtension {

    /**
     * Retrieve the extension of type [Extension] previously created using
     * [ExtensionConfigurationScope.createExtension].
     */
    public fun <Extension : Any> extension(type: KClass<Extension>): Extension
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Retrieve the extension of type [Extension] previously created using
 * [ExtensionConfigurationScope.createExtension].
 */
public inline fun <reified Extension : Any> HasExtension.extension(): Extension {
    return extension(Extension::class)
}