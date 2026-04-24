package komple.tool.emscripten

import komple.exec.Command
import komple.exec.ExecEnvironmentBuilderScope
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
import komple.tool.task.unzip
import komple.tool.task.url
import org.gradle.api.tasks.TaskProvider
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
                checksum.convention(kompleProperty("emsdk.checksum"))
            }
        }
    }

    override fun DownloadTaskRegistrationScope<EmscriptenExtension>.registerDownloadTask(): TaskProvider<*> {
        val version = extension.version.get()
        return url("https://github.com/emscripten-core/emsdk/archive/refs/tags/$version.zip")
    }

    override fun IntegrityTaskRegistrationScope<EmscriptenExtension>.registerIntegrityTask(): TaskProvider<*> {
        return checksum(extension.checksum, Algorithm.SHA_256)
    }

    override fun ExtractTaskRegistrationScope<EmscriptenExtension>.registerExtractTask(): TaskProvider<*> {
        return unzip(true)
    }

    override fun InstallTaskRegistrationScope<EmscriptenExtension>.registerInstallTask(): TaskProvider<*> {
        return command {
            val emsdk = when (host.operatingSystem) {
                MacOS, Linux -> "./emsdk"
                Windows -> "emsdk.bat"
            }

            Command(emsdk, "install", "latest") {
                then(emsdk, "activate", "latest")
            }
        }
    }

    override fun ExecEnvironmentBuilderScope<EmscriptenExtension>.configureEnvironment() {
        val (args, envScript) = when (host.operatingSystem) {
            MacOS, Linux -> arrayOf("source") to "emsdk_env.sh"
            Windows -> emptyArray<String>() to "emsdk_env.bat"
        }

        commandLine(installDirectory.map { directory ->
            Command(*args, directory.file(envScript).asFile.absolutePath)
        })
    }
}