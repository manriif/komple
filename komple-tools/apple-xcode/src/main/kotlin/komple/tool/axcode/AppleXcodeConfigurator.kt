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
package komple.tool.axcode

import komple.platform.Host
import komple.project.CProjectConfigurator
import komple.project.ProjectConfigurationScope
import komple.project.createExtension
import komple.project.registerCompileTask
import komple.tool.axcode.compile.AppleXcodeCCompileTask
import komple.tool.axcode.compile.AppleXcodeCompilationParams
import komple.tool.axcode.compile.configureConventions
import komple.tool.configurator.DefaultKompleToolConfigurator
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.createExtension
import org.gradle.kotlin.dsl.assign
import javax.inject.Inject

/**
 * Configurator for Apple Xcode.
 */
public abstract class AppleXcodeConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<AppleXcodeExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS -> true
        Linux, Windows -> false
    }

    override fun ExtensionConfigurationScope<AppleXcodeExtension>.configureExtension(): AppleXcodeExtension {
        return createExtension {
            add(AppleXcodeExtension::compilationParams) {
                extension.configureConventions(project)
            }
        }
    }

    override fun ProjectConfigurationScope<AppleXcodeExtension>.configureProject() {
        if (!supportHost(host)) {
            return
        }

        when (val configurator = configurator) {
            is CProjectConfigurator -> {
                val cParams = createExtension<AppleXcodeCompilationParams>("apple").apply {
                    configureConventions(extension.compilationParams)
                }

                configurator.registerCompileTask<AppleXcodeCCompileTask>(
                    configure = {
                        this.params = cParams
                    },
                    platformFilter = { platform ->
                        platform.operatingSystem is Darwin
                    }
                )
            }
        }
    }
}