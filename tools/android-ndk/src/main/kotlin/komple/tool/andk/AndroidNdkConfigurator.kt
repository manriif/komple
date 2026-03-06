package komple.tool.andk

import komple.exec.variable
import komple.platform.Architecture
import komple.platform.Host
import komple.platform.OperatingSystem
import komple.tool.compile.ExecEnvironmentBuilderScope
import komple.tool.configurator.DefaultKompleToolConfigurator
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.createExtension
import komple.tool.extension.kompleStringProperty
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
                version.convention(kompleStringProperty("androidNdk.version"))

                checksums.convention(
                    AndroidNdkChecksums(
                        linux = kompleStringProperty("androidNdk.checksum.linux"),
                        macos = kompleStringProperty("androidNdk.checksum.macos"),
                        windows = kompleStringProperty("androidNdk.checksum.windows"),
                    )
                )
            }
        }
    }

    override fun DownloadTaskRegistrationScope<AndroidNdkExtension>.registerDownloadTask(): TaskProvider<*> {
        val (platform, fileExtension) = when (host.operatingSystem) {
            OperatingSystem.MacOS -> "darwin" to "dmg"

            OperatingSystem.Linux -> when (host.architecture) {
                Architecture.Arm64 -> return unsupported()
                Architecture.X64 -> "linux" to "zip"
            }

            OperatingSystem.Windows -> when (host.architecture) {
                Architecture.Arm64 -> return unsupported()
                Architecture.X64 -> "windows" to "zip"
            }
        }

        return url(extension.version.map { version ->
            val major = version.substringBefore('.')
            val file = "android-ndk-${major}-$platform.$fileExtension"
            "https://dl.google.com/android/repository/$file"
        })
    }

    override fun IntegrityTaskRegistrationScope<AndroidNdkExtension>.registerIntegrityTask(): TaskProvider<*> {
        return checksum(
            checksum = when (host.operatingSystem) {
                OperatingSystem.Linux -> extension.checksums.map(AndroidNdkChecksums::linux)
                OperatingSystem.MacOS -> extension.checksums.map(AndroidNdkChecksums::macos)
                OperatingSystem.Windows -> extension.checksums.map(AndroidNdkChecksums::windows)
            },
            algorithm = Algorithm.SHA1
        )
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