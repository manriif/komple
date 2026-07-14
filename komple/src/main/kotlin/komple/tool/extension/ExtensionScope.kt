/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package komple.tool.extension

import komple.KompleInternalApi
import komple.kompleProperty
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import kotlin.reflect.KProperty1

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

    /**
     * Adds the child extension [Child] already declared in [Extension] and optionally [configure]s
     * it.
     */
    @IgnorableReturnValue
    public fun <Child : Any> add(
        property: KProperty1<Extension, Child>,
        configure: (ExtensionScope<Child>.() -> Unit)? = null
    ): Child
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