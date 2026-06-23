package komple.tool.wabt

import komple.exec.Command
import komple.exec.ShellEnvironmentBuilderScope
import komple.exec.path
import komple.kompleProperty
import komple.platform.Host
import komple.task.integrity.DigestAlgorithm
import komple.tool.configurator.VersionedKompleToolConfigurator
import komple.tool.task.DownloadTaskRegistrationScope
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.InstallTaskRegistrationScope
import komple.tool.task.IntegrityTaskRegistrationScope
import komple.tool.task.checksum
import komple.tool.task.command
import komple.tool.task.untarXz
import komple.tool.task.url
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.inject.Inject

/**
 * Configurator for WABT.
 */
public abstract class WabtConfigurator @Inject constructor(name: String) :
    VersionedKompleToolConfigurator(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS, Linux -> true
        Windows -> false
    }

    override fun Extension.configure(project: Project) {
        version.convention(project.kompleProperty("wabt.version"))
        checksum.convention(project.kompleProperty("wabt.checksum"))
    }

    override fun DownloadTaskRegistrationScope<Extension>.registerDownloadTask(): TaskProvider<*> {
        return url(extension.version.map { version ->
            "https://github.com/WebAssembly/wabt/releases/download/$version/wabt-$version.tar.xz"
        })
    }

    override fun IntegrityTaskRegistrationScope<Extension>.registerIntegrityTask(): TaskProvider<*> {
        return checksum(extension.checksum, DigestAlgorithm.SHA_256)
    }

    override fun ExtractTaskRegistrationScope<Extension>.registerExtractTask(): TaskProvider<*> {
        return untarXz(true)
    }

    override fun InstallTaskRegistrationScope<Extension>.registerInstallTask(): TaskProvider<*> {
        return command { outputDirectory ->
            val buildPath = outputDirectory
                .resolve("build")
                .apply(File::mkdirs)
                .absolutePath

            Command("cmake", "-S", ".", "-B", buildPath) {
                then("cmake", "--build", buildPath)
            }
        }
    }

    override fun ShellEnvironmentBuilderScope<Extension>.configureEnvironment() {
        path(installDirectory.map { it.dir("bin") })
    }
}