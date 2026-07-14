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
package komple.tool.configurator

import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.HasChecksumSupport
import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import komple.tool.extension.createExtension
import org.gradle.api.Project

/**
 * Base for implementor of [KompleToolConfigurator] that do not need a custom extension and accept
 * a version.
 */
public abstract class VersionedKompleToolConfigurator(toolName: String) :
    DefaultKompleToolConfigurator<VersionedKompleToolConfigurator.Extension>(toolName) {

    override fun getName(): String {
        return toolName
    }

    /**
     * Configures the [Extension] with defaults values.
     */
    protected abstract fun Extension.configure(project: Project)

    final override fun ExtensionConfigurationScope<Extension>.configureExtension(): Extension {
        return createExtension {
            extension.configure(project)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extension
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Extension for tools that do not need a custom extension and accept a version.
     */
    public interface Extension :
        KompleToolExtension,
        HasVersionSupport,
        HasChecksumSupport
}