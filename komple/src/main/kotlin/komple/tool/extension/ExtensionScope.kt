package komple.tool.extension

import komple.KompleInternalApi
import komple.kompleProperty
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
 */
@KompleInternalApi
public fun ExtensionScope<*>.kompleProperty(name: String): String {
    return project.kompleProperty(name)
}