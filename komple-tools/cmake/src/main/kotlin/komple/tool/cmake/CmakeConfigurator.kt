package komple.tool.cmake

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
import komple.tool.task.IntegrityTaskRegistrationScope
import komple.tool.task.checksum
import komple.tool.task.untarGzip
import komple.tool.task.unzip
import komple.tool.task.url
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import javax.inject.Inject

/**
 * Configurator for CMake.
 */
public abstract class CmakeConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<CmakeExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS, Linux, Windows -> true
    }

    override fun ExtensionConfigurationScope<CmakeExtension>.configureExtension(): CmakeExtension {
        return createExtension {
            extension.run {
                version.convention(kompleProperty("cmake.version"))

                add(CmakeExtension::checksums) {
                    extension.run {
                        linuxAarch64 = kompleProperty("cmake.checksum.linux.aarch64")
                        linuxX64 = kompleProperty("cmake.checksum.linux.x64")
                        macos = kompleProperty("cmake.checksum.macos")
                        windowsAarch64 = kompleProperty("cmake.checksum.windows.aarch64")
                        windowsX64 = kompleProperty("cmake.checksum.windows.x64")
                    }
                }
            }
        }
    }

    override fun DownloadTaskRegistrationScope<CmakeExtension>.registerDownloadTask(): TaskProvider<*> {
        val platformFile = when (host.operatingSystem) {
            Linux -> when (host.architecture) {
                Arm64 -> "linux-aarch64.tar.gz"
                X64 -> "linux-x86_64.tar.gz"
            }

            MacOS -> "macos-universal.tar.gz"

            Windows -> when (host.architecture) {
                Arm64 -> "windows-arm64.zip"
                X64 -> "windows-x86_64.zip"
            }
        }

        return url(extension.version.map { version ->
            "https://github.com/Kitware/CMake/releases/download/v$version/cmake-$version-$platformFile"
        })
    }

    override fun IntegrityTaskRegistrationScope<CmakeExtension>.registerIntegrityTask(): TaskProvider<*> {
        return extension.checksums.run {
            checksum(
                checksum = when (host.operatingSystem) {
                    Linux -> when (host.architecture) {
                        Arm64 -> linuxAarch64
                        X64 -> linuxX64
                    }

                    MacOS -> macos

                    Windows -> when (host.architecture) {
                        Arm64 -> windowsAarch64
                        X64 -> windowsX64
                    }
                },
                algorithm = Algorithm.SHA_256
            )
        }
    }

    override fun ExtractTaskRegistrationScope<CmakeExtension>.registerExtractTask(): TaskProvider<*> {
        return when (host.operatingSystem) {
            MacOS, Linux -> untarGzip(true)
            Windows -> unzip(true)
        }
    }

    override fun ExecEnvironmentBuilderScope<CmakeExtension>.configureEnvironment() {
        path(
            when (host.operatingSystem) {
                MacOS -> installDirectory.map { it.dir("CMake.app/Contents/bin") }
                Linux, Windows -> installDirectory.map { it.dir("bin") }
            }
        )
    }
}