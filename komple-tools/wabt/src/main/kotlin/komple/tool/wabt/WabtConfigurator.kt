package komple.tool.wabt

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
import komple.tool.task.url
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.inject.Inject

/**
 * Configurator for WABT.
 */
public abstract class WabtConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<WabtExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS, Linux -> true
        Windows -> false
    }

    override fun ExtensionConfigurationScope<WabtExtension>.configureExtension(): WabtExtension {
        return createExtension {
            extension.run {
                version.convention(kompleProperty("wabt.version"))
                checksum.convention(kompleProperty("wabt.checksum"))
            }
        }
    }

    override fun DownloadTaskRegistrationScope<WabtExtension>.registerDownloadTask(): TaskProvider<*> {
        return url(extension.version.map { version ->
            "https://github.com/WebAssembly/wabt/releases/download/$version/wabt-$version.tar.xz"
        })
    }

    override fun IntegrityTaskRegistrationScope<WabtExtension>.registerIntegrityTask(): TaskProvider<*> {
        return checksum(extension.checksum, Algorithm.SHA_256)
    }

    override fun ExtractTaskRegistrationScope<WabtExtension>.registerExtractTask(): TaskProvider<*> {
        // TODO this assumes that tar is installed, maybe create a new Komple tool for tar
        return command {
            Command(
                "tar",
                "-xJf",
                downloadDirectory.singleFile.get().asFile.absolutePath,
                "-C",
                outputDirectory.asFile.absolutePath,
                "--strip-components=1"
            )
        }
    }

    override fun InstallTaskRegistrationScope<WabtExtension>.registerInstallTask(): TaskProvider<*> {
        return command {
            val buildDirectory = outputDirectory.dir("build").asFile
                .apply(File::mkdirs)

            Command("cmake", "-S", ".", "-B", buildDirectory.absolutePath) {
                then("cmake", "--build", buildDirectory.absolutePath)
            }
        }
    }

    override fun ExecEnvironmentBuilderScope<WabtExtension>.configureEnvironment() {
        path(installDirectory.map { it.dir("bin") })
    }
}