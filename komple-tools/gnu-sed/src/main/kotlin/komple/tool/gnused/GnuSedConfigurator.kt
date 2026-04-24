package komple.tool.gnused

import komple.exec.Command
import komple.exec.ExecEnvironmentBuilderScope
import komple.exec.path
import komple.platform.Host
import komple.tool.configurator.DefaultKompleToolConfigurator
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.createExtension
import komple.tool.extension.kompleProperty
import komple.tool.task.Algorithm
import komple.tool.task.DownloadTaskRegistrationScope
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.InstallTaskRegistrationScope
import komple.tool.task.IntegrityTaskRegistrationScope
import komple.tool.task.checksum
import komple.tool.task.command
import komple.tool.task.untarGzip
import komple.tool.task.url
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.inject.Inject

/**
 * Configurator for GNU sed.
 */
public abstract class GnuSedConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<GnuSedExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS, Linux -> true
        Windows -> false
    }

    override fun ExtensionConfigurationScope<GnuSedExtension>.configureExtension(): GnuSedExtension {
        return createExtension {
            extension.run {
                version.convention(kompleProperty("gnu.sed.version"))
                checksum.convention(kompleProperty("gnu.sed.checksum"))
            }
        }
    }

    override fun DownloadTaskRegistrationScope<GnuSedExtension>.registerDownloadTask(): TaskProvider<*> {
        val version = extension.version.get()
        return url("https://ftp.gnu.org/gnu/sed/sed-$version.tar.gz")
    }

    override fun IntegrityTaskRegistrationScope<GnuSedExtension>.registerIntegrityTask(): TaskProvider<*> {
        return checksum(extension.checksum, Algorithm.SHA_256)
    }

    override fun ExtractTaskRegistrationScope<GnuSedExtension>.registerExtractTask(): TaskProvider<*> {
        return untarGzip(true)
    }

    override fun InstallTaskRegistrationScope<GnuSedExtension>.registerInstallTask(): TaskProvider<*> {
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

    override fun ExecEnvironmentBuilderScope<GnuSedExtension>.configureEnvironment() {
        path(installDirectory.map { it.dir("build/bin") })
    }
}