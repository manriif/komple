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
package komple.gradle.extension

import komple.gradle.util.ClosableScope
import komple.tool.extension.ExtensionScope
import org.gradle.api.Project
import kotlin.reflect.KProperty1

/**
 * Implementation of [ExtensionScope].
 */
internal class DefaultExtensionScope<Extension : Any>(
    override val extension: Extension,
    override val project: Project
) : ExtensionScope<Extension>,
    ClosableScope() {

    override fun <Child : Any> add(
        property: KProperty1<Extension, Child>,
        configure: (ExtensionScope<Child>.() -> Unit)?
    ): Child = notClosed {
        property.get(extension).also { child ->
            extension.extensions.add(property.name, child)
            DefaultExtensionScope(project, child, configure)
        }
    }

    companion object {

        /**
         * Invokes [configure] with a [DefaultExtensionScope] instance if [configure] is not `null`.
         */
        operator fun <Extension : Any> invoke(
            project: Project,
            extension: Extension,
            configure: (ExtensionScope<Extension>.() -> Unit)?
        ) {
            configure?.let { action ->
                DefaultExtensionScope(extension, project).use(action)
            }
        }
    }
}