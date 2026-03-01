package komple.extension

import kotlin.reflect.KClass

/**
 * Represents a Komple entity able to provide registered extension.
 */
public interface HasExtension {

    /**
     * Retrieve the extension of type [Extension] previously created using
     * [ExtensionRegistrationScope.createExtension].
     */
    public fun <Extension : Any> retrieveExtension(type: KClass<Extension>): Extension
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Retrieve the extension of type [Extension] previously created using
 * [ExtensionRegistrationScope.createExtension].
 */
public inline fun <reified Extension : Any> HasExtension.retrieveExtension(): Extension {
    return retrieveExtension(Extension::class)
}