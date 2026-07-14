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
package komple.tool.zig

import komple.exec.ShellEnvironmentBuilderScope
import komple.exec.path
import komple.platform.Host
import komple.project.CProjectConfigurator
import komple.project.ProjectConfigurationScope
import komple.project.createExtension
import komple.project.registerCompileTask
import komple.task.noCache
import komple.tool.configurator.DefaultKompleToolConfigurator
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.createExtension
import komple.tool.extension.kompleProperty
import komple.tool.task.DownloadTaskRegistrationScope
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.download
import komple.tool.task.unarchive
import komple.tool.zig.compile.ZigCCompileTask
import komple.tool.zig.compile.ZigCompilationParams
import komple.tool.zig.tasks.ZigDownloadTask
import komple.tool.zig.tasks.ZigUntarXzExtractTask
import komple.tool.zig.tasks.ZigUnzipExtractTask
import komple.tool.zig.tasks.configure
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import javax.inject.Inject

/**
 * Configurator for Zig.
 */
public abstract class ZigConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<ZigExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS, Linux, Windows -> true
    }

    override fun ExtensionConfigurationScope<ZigExtension>.configureExtension(): ZigExtension {
        return createExtension {
            extension.run {
                version.convention(kompleProperty("zig.version"))
                publicKey.convention(kompleProperty("zig.signing.pubkey"))
                add(ZigExtension::compilationParams)
            }
        }
    }

    override fun DownloadTaskRegistrationScope<ZigExtension>.registerDownloadTask(): TaskProvider<*> {
        val (platformName, archiveExtension) = when (host.operatingSystem) {
            MacOS -> "macos" to "tar.xz"
            Linux -> "linux" to "tar.xz"
            Windows -> "windows" to "zip"
        }

        val architectureName = when (host.architecture) {
            Arm64 -> "aarch64"
            X64 -> "x86_64"
        }

        extension.archiveFileName = extension.version.map { version ->
            "zig-$architectureName-$platformName-$version.$archiveExtension"
        }

        return download<ZigDownloadTask>(false) {
            version = extension.version
            publicKey = extension.publicKey
            extension.configure(this)
        }
    }

    override fun ExtractTaskRegistrationScope<ZigExtension>.registerExtractTask(): TaskProvider<*> {
        return noCache(
            when (host.operatingSystem) {
                MacOS, Linux -> unarchive<ZigUntarXzExtractTask>(true)
                Windows -> unarchive<ZigUnzipExtractTask>(true)
            }.apply {
                configure { extension.configure(this) }
            }
        )
    }

    override fun ShellEnvironmentBuilderScope<ZigExtension>.configureEnvironment() {
        path(installDirectory)
    }

    override fun ProjectConfigurationScope<ZigExtension>.configureProject() {
        when (val configurator = configurator) {
            is CProjectConfigurator -> {
                val cParams = createExtension<ZigCompilationParams>("zig")

                configurator.registerCompileTask<ZigCCompileTask>(
                    configure = {
                        this.params = cParams
                    },
                    platformFilter = { platform ->
                        (platform.operatingSystem == Linux || platform.operatingSystem == Windows)
                                && (platform.architecture == Arm64 || platform.architecture == X64)
                    }
                )
            }
        }
    }
}