package komple.util

import komple.KompleInternalApi
import org.gradle.api.Project

/**
 * Returns the extension of type [Extension] registered as [name].
 */
@KompleInternalApi
public inline fun <reified Extension> Project.getExtensionByName(name: String): Extension {
    return checkNotNull(extensions.findByName(name) as? Extension) {
        "Failed to retrieve extension '$name' with type ${Extension::class.simpleName}"
    }
}