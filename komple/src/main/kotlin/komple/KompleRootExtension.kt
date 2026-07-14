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
package komple

import komple.tool.configurator.KompleToolConfigurator
import komple.util.getExtensionByName
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import kotlin.reflect.KClass

/**
 * Base for Komple root extension.
 */
public interface KompleRootExtension {

    /**
     * Registers a tool, identified by [name], that is configured by an instance of [klass].
     */
    @IgnorableReturnValue
    public fun <Configurator : KompleToolConfigurator<*>> registerTool(
        name: String,
        klass: KClass<Configurator>,
        vararg args: Any
    ): NamedDomainObjectProvider<Configurator>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Returns the [KompleRootExtension] for `this` [Project].
 */
internal val Project.kompleRootExtension: KompleRootExtension
    get() = rootProject.getExtensionByName(KOMPLE_EXTENSION_NAME)

/**
 * Registers a tool, identified by [name], that is configured by an instance of [Config].
 */
@IgnorableReturnValue
public inline fun <reified Config : KompleToolConfigurator<*>> KompleRootExtension.registerTool(
    name: String,
    vararg args: Any
): NamedDomainObjectProvider<Config> = registerTool(
    name = name,
    klass = Config::class,
    args = args
)