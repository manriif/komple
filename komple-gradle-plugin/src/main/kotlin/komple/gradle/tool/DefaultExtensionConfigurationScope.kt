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
package komple.gradle.tool

import komple.gradle.extension.DefaultExtensionScope
import komple.gradle.extension.KompleRootProjectExtension
import komple.gradle.extension.extensions
import komple.gradle.util.ClosableScope
import komple.gradle.util.camelCased
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.ExtensionScope
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import kotlin.reflect.KClass

/**
 * Default implementation of [komple.tool.extension.ExtensionConfigurationScope].
 */
internal class DefaultExtensionConfigurationScope<Extension : KompleToolExtension>(
    private val project: Project,
    private val rootExtension: KompleRootProjectExtension,
    private val toolName: String,
) : ExtensionConfigurationScope<Extension>,
    ClosableScope() {

    override fun createExtension(
        type: KClass<Extension>,
        vararg args: Any,
        configure: (ExtensionScope<Extension>.() -> Unit)?
    ): Extension = notClosed {
        rootExtension.extensions.create(
            name = toolName.camelCased(),
            type = type,
            constructionArguments = args
        ).also { extension ->
            DefaultExtensionScope(project, extension, configure)
        }
    }
}