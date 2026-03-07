package komple.tool.extension

import komple.KompleInternalApi
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

/**
 * Extends [Extension] DSL.
 */
public interface ExtensionScope<Extension : Any> {

    /**
     * The Gradle [Project] on which the Komple plugin was applied.
     */
    public val project: Project

    /**
     * Context extension.
     */
    public val extension: Extension
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Returns the value of the komple property [name] as a [String] using [extra].
 *
 * @throws ClassCastException if the returned value is not a [String].
 * @throws NullPointerException if the returned value is `null` or do not exist.
 * @throws groovy.lang.MissingPropertyException if the property is missing.
 */
@KompleInternalApi
public fun ExtensionScope<*>.kompleProperty(name: String): String {
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