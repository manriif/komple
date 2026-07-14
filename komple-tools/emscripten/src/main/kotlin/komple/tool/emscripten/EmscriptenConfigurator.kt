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
package komple.tool.emscripten

import komple.exec.Command
import komple.exec.ShellEnvironmentBuilderScope
import komple.platform.Host
import komple.task.integrity.DigestAlgorithm
import komple.tool.configurator.DefaultKompleToolConfigurator
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.createExtension
import komple.tool.extension.kompleProperty
import komple.tool.task.DownloadTaskRegistrationScope
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.InstallTaskRegistrationScope
import komple.tool.task.IntegrityTaskRegistrationScope
import komple.tool.task.checksum
import komple.tool.task.command
import komple.tool.task.unzip
import komple.tool.task.url
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import javax.inject.Inject

/**
 * Configurator for Emscripten.
 */
public abstract class EmscriptenConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<EmscriptenExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS, Linux, Windows -> true
    }

    override fun ExtensionConfigurationScope<EmscriptenExtension>.configureExtension(): EmscriptenExtension {
        return createExtension {
            extension.run {
                version.convention(kompleProperty("emsdk.version"))
                emscriptenVersion.convention(version)
                checksum.convention(kompleProperty("emsdk.checksum"))
            }
        }
    }

    override fun DownloadTaskRegistrationScope<EmscriptenExtension>.registerDownloadTask(): TaskProvider<*> {
        return url(extension.version.map { version ->
            "https://github.com/emscripten-core/emsdk/archive/refs/tags/$version.zip"
        })
    }

    override fun IntegrityTaskRegistrationScope<EmscriptenExtension>.registerIntegrityTask(): TaskProvider<*> {
        return checksum(extension.checksum, DigestAlgorithm.SHA_256)
    }

    override fun ExtractTaskRegistrationScope<EmscriptenExtension>.registerExtractTask(): TaskProvider<*> {
        return unzip(true)
    }

    override fun InstallTaskRegistrationScope<EmscriptenExtension>.registerInstallTask(): TaskProvider<*> {
        val operatingSystem = host.operatingSystem

        return command<EmscriptenInstallTask> {
            this.operatingSystem = operatingSystem
            this.emscriptenVersion = extension.emscriptenVersion
        }
    }

    override fun ShellEnvironmentBuilderScope<EmscriptenExtension>.configureEnvironment() {
        val (envScript, args: Array<String>) = when (host.operatingSystem) {
            MacOS, Linux -> "emsdk_env.sh" to arrayOf("source")
            Windows -> "emsdk_env.bat" to emptyArray()
        }

        command(installDirectory.map { directory ->
            Command(*args, directory.file(envScript).asFile.absolutePath)
        })
    }
}