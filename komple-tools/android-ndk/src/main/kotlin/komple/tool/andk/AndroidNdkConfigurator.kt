package komple.tool.andk

import komple.exec.variable
import komple.platform.Architecture
import komple.platform.Host
import komple.platform.OperatingSystem
import komple.tool.compile.ExecEnvironmentBuilderScope
import komple.tool.configurator.DefaultKompleToolConfigurator
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.createExtension
import komple.tool.extension.kompleProperty
import komple.tool.task.Algorithm
import komple.tool.task.DownloadTaskRegistrationScope
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.IntegrityTaskRegistrationScope
import komple.tool.task.checksum
import komple.tool.task.dmg
import komple.tool.task.unzip
import komple.tool.task.url
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

/**
 * Configurator for the Android NDK.
 */
public abstract class AndroidNdkConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<AndroidNdkExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        OperatingSystem.MacOS -> true
        OperatingSystem.Linux, OperatingSystem.Windows -> when (host.architecture) {
            Architecture.Arm64 -> false
            Architecture.X64 -> true
        }
    }

    override fun ExtensionConfigurationScope<AndroidNdkExtension>.configureExtension(): AndroidNdkExtension {
        return createExtension {
            extension.run {
                version.convention(kompleProperty("androidNdk.version"))

                checksums.convention(
                    AndroidNdkChecksums(
                        linux = kompleProperty("androidNdk.checksum.linux"),
                        macos = kompleProperty("androidNdk.checksum.macos"),
                        windows = kompleProperty("androidNdk.checksum.windows"),
                    )
                )
            }
        }
    }

    override fun DownloadTaskRegistrationScope<AndroidNdkExtension>.registerDownloadTask(): TaskProvider<*> {
        val (platform, fileExtension) = when (host.operatingSystem) {
            OperatingSystem.MacOS -> "darwin" to "dmg"
            OperatingSystem.Linux -> "linux" to "zip"
            OperatingSystem.Windows -> "windows" to "zip"
        }

        return url(extension.version.map { version ->
            val major = version.substringBefore('.')
            val file = "android-ndk-${major}-$platform.$fileExtension"
            "https://dl.google.com/android/repository/$file"
        })
    }

    override fun IntegrityTaskRegistrationScope<AndroidNdkExtension>.registerIntegrityTask(): TaskProvider<*> {
        return extension.checksums.run {
            checksum(
                checksum = when (host.operatingSystem) {
                    OperatingSystem.Linux -> map(AndroidNdkChecksums::linux)
                    OperatingSystem.MacOS -> map(AndroidNdkChecksums::macos)
                    OperatingSystem.Windows -> map(AndroidNdkChecksums::windows)
                },
                algorithm = Algorithm.SHA1
            )
        }
    }

    override fun ExtractTaskRegistrationScope<AndroidNdkExtension>.registerExtractTask(): TaskProvider<*> {
        return when (host.operatingSystem) {
            OperatingSystem.Linux, OperatingSystem.Windows -> unzip(true)
            OperatingSystem.MacOS -> {
                val version = extension.version

                dmg({ property("version", version) }) { mountPoint, extractDirectory ->
                    val build = version.get().split('.').last()
                    val ndkDirectory = mountPoint.resolve("AndroidNDK$build.app/Contents/NDK")

                    copy {
                        from(ndkDirectory)
                        into(extractDirectory)
                    }
                }
            }
        }
    }

    override fun ExecEnvironmentBuilderScope<AndroidNdkExtension>.configureEnvironment() {
        variable("ANDROID_NDK_HOME", installDirectory)
        variable("ANDROID_NDK_ROOT", installDirectory)
    }
}