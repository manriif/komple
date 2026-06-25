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