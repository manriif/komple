package komple.tool.gnused

import komple.exec.Command
import komple.exec.ExecEnvironmentBuilderScope
import komple.exec.path
import komple.kompleProperty
import komple.platform.Host
import komple.tool.configurator.VersionedKompleToolConfigurator
import komple.tool.task.Algorithm
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
        return checksum(extension.checksum, Algorithm.SHA_256)
    }

    override fun ExtractTaskRegistrationScope<Extension>.registerExtractTask(): TaskProvider<*> {
        return untarGzip(true)
    }

    override fun InstallTaskRegistrationScope<Extension>.registerInstallTask(): TaskProvider<*> {
        return command {
            val buildDirectory = outputDirectory.dir("build").asFile
                .apply(File::mkdirs)

            Command(
                "./configure",
                "--prefix=${buildDirectory.absolutePath}",
                "--disable-nls",
                "--without-selinux"
            ) {
                then("make", "-j4")
                then("make", "install")
            }
        }
    }

    override fun ExecEnvironmentBuilderScope<Extension>.configureEnvironment() {
        path(installDirectory.map { it.dir("build/bin") })
    }
}