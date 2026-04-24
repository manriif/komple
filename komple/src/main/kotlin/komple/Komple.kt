package komple

import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

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

/**
 * Name of the [komple.exec.ExecService].
 */
public const val KOMPLE_EXEC_SERVICE_NAME: String = "komple-exec-service"

/**
 * Returns the value of the komple property [name] as a [String] using [extra].
 *
 * @throws ClassCastException if the returned value is not a [String].
 * @throws NullPointerException if the returned value is `null` or do not exist.
 */
@KompleInternalApi
public fun Project.kompleProperty(name: String): String {
    val propertyName = "komple.$name"

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