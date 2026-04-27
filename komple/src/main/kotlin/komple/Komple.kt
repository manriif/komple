package komple

import org.gradle.api.Project
import org.gradle.api.resources.MissingResourceException
import org.gradle.internal.extensions.core.extra
import java.util.*

private const val PROPERTIES_FILE = "komple.properties"
private const val PROPERTIES_PREFIX = "komple"

/**
 * Identifier of the Komple group.
 */
public const val KOMPLE_GROUP: String = "io.github.manriif.komple"

/**
 * Identifier of the Komple Gradle plugin.
 */
public const val KOMPLE_PLUGIN_ID: String = KOMPLE_GROUP

/**
 * Name of the [KompleRootExtension].
 */
public const val KOMPLE_EXTENSION_NAME: String = "komple"

///////////////////////////////////////////////////////////////////////////
// Properties
///////////////////////////////////////////////////////////////////////////

/**
 * Injects [PROPERTIES_FILE]'s properties into project.
 */
@KompleInternalApi
public fun Project.loadKompleProperties(loader: ClassLoader) {
    val resource = try {
        loader.getResource(PROPERTIES_FILE)
    } catch (cause: Throwable) {
        throw MissingResourceException("File $PROPERTIES_FILE is missing", cause)
    }

    val properties = Properties().apply {
        load(resource.openStream())
    }

    properties.forEach { (key, value) ->
        val propertyName = "$PROPERTIES_PREFIX.$key"

        if (!extra.has(propertyName)) {
            extra.set(propertyName, value?.toString())
        }
    }
}

/**
 * Returns the value of the komple property [name] as a [String] using [extra].
 *
 * @throws ClassCastException if the returned value is not a [String].
 * @throws NullPointerException if the returned value is `null` or do not exist.
 */
@KompleInternalApi
public fun Project.kompleProperty(name: String): String {
    val propertyName = "$PROPERTIES_PREFIX.$name"

    if (!project.extra.has(propertyName)) {
        throw NullPointerException("Property $propertyName does not exist")
    }

    val value = project.extra.get(propertyName)
        ?: throw NullPointerException("Value of property $propertyName is null")

    if (value !is String) {
        throw ClassCastException(
            "Value of property $propertyName is not a String " +
                    "(${value::class})"
        )
    }

    return value
}