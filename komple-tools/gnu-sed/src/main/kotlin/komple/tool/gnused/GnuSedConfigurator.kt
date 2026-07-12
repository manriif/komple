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
package komple.tool.gnused

import komple.exec.Command
import komple.exec.ShellEnvironmentBuilderScope
import komple.exec.path
import komple.kompleProperty
import komple.platform.Host
import komple.tool.configurator.VersionedKompleToolConfigurator
import komple.task.integrity.DigestAlgorithm
import komple.tool.task.DownloadTaskRegistrationScope
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.InstallTaskRegistrationScope
import komple.tool.task.IntegrityTaskRegistrationScope
import komple.tool.task.checksum
import komple.tool.task.command
import komple.tool.task.untarGzip
import komple.tool.task.url
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.inject.Inject

/**
 * Configurator for GNU sed.
 */
public abstract class GnuSedConfigurator @Inject constructor(name: String) :
    VersionedKompleToolConfigurator(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS, Linux -> true
        Windows -> false
    }

    override fun Extension.configure(project: Project) {
        version.convention(project.kompleProperty("gnu.sed.version"))
        checksum.convention(project.kompleProperty("gnu.sed.checksum"))
    }

    override fun DownloadTaskRegistrationScope<Extension>.registerDownloadTask(): TaskProvider<*> {
        return url(extension.version.map { version ->
            "https://ftp.gnu.org/gnu/sed/sed-$version.tar.gz"
        })
    }

    override fun IntegrityTaskRegistrationScope<Extension>.registerIntegrityTask(): TaskProvider<*> {
        return checksum(extension.checksum, DigestAlgorithm.SHA_256)
    }

    override fun ExtractTaskRegistrationScope<Extension>.registerExtractTask(): TaskProvider<*> {
        return untarGzip(true)
    }

    override fun InstallTaskRegistrationScope<Extension>.registerInstallTask(): TaskProvider<*> {
        return command { outputDirectory ->
            val buildDirectory = outputDirectory
                .resolve("build")
                .apply(File::mkdirs)

            Command("find . -exec touch {} +") {
                then("./configure",
                    "--prefix=${buildDirectory.absolutePath}",
                    "--disable-nls",
                    "--without-selinux"
                )

                then("make")
                then("make", "install")
            }
        }
    }

    override fun ShellEnvironmentBuilderScope<Extension>.configureEnvironment() {
        path(installDirectory.map { it.dir("build/bin") })
    }
}