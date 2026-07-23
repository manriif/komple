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
package komple.gradle.project

import komple.exec.Command
import komple.exec.ShellEnvironment
import komple.exec.ShellEnvironmentBuilderScope
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.platform.Host
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

/**
 * Default implementation of [ShellEnvironmentBuilderScope].
 */
internal class DefaultShellEnvironmentBuilderScope<Extension : KompleToolExtension>(
    private val context: KompleToolConfigContext<Extension>,
    private val environment: ShellEnvironment,
    override val cacheDirectory: Provider<Directory>,
    override val installDirectory: Provider<Directory>
) : ShellEnvironmentBuilderScope<Extension>,
    HasExtension<Extension> by context,
    ClosableScope() {

    override val host: Host
        get() = notClosed { context.host }

    override val providers: ProviderFactory
        get() = notClosed { context.project.providers }

    override fun path(pathProvider: Provider<String>) =
        environment.notClosed { paths.add(pathProvider) }

    override fun variable(
        name: String,
        valueProvider: Provider<String>
    ) = environment.notClosed { variables.put(name, valueProvider) }

    override fun command(commandProvider: Provider<Command>) =
        environment.notClosed { commands.add(commandProvider) }
}