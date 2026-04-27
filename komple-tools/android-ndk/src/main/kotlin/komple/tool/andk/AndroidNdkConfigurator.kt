package komple.tool.andk

import komple.exec.ExecEnvironmentBuilderScope
import komple.exec.variable
import komple.platform.Host
import komple.platform.OperatingSystem
import komple.project.CProjectConfigurator
import komple.project.ProjectConfigurationScope
import komple.project.createExtension
import komple.project.registerCompileTask
import komple.tool.andk.compile.AndroidNdkCCompileTask
import komple.tool.andk.compile.AndroidNdkCompilationParams
import komple.tool.andk.compile.configureConventions
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
import org.gradle.kotlin.dsl.assign
import javax.inject.Inject

/**
 * Configurator for the Android NDK.
 */
public abstract class AndroidNdkConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<AndroidNdkExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS -> true
        Linux, Windows -> when (host.architecture) {
            Arm64 -> false
            X64 -> true
        }
    }

    override fun ExtensionConfigurationScope<AndroidNdkExtension>.configureExtension(): AndroidNdkExtension {
        return createExtension {
            extension.run {
                version.convention(kompleProperty("androidNdk.version"))

                add(AndroidNdkExtension::checksums) {
                    extension.run {
                        linux = kompleProperty("androidNdk.checksum.linux")
                        macos = kompleProperty("androidNdk.checksum.macos")
                        windows = kompleProperty("androidNdk.checksum.windows")
                    }
                }

                add(AndroidNdkExtension::compilationParams) {
                    extension.configureConventions(project)
                }
            }
        }
    }

    override fun DownloadTaskRegistrationScope<AndroidNdkExtension>.registerDownloadTask(): TaskProvider<*> {
        val platformFile = when (host.operatingSystem) {
            MacOS -> "darwin.dmg"
            Linux -> "linux.zip"
            Windows -> "windows.zip"
        }

        return url(extension.version.map { version ->
            val major = version.substringBefore('.')
            val file = "android-ndk-${major}-$platformFile"
            "https://dl.google.com/android/repository/$file"
        })
    }

    override fun IntegrityTaskRegistrationScope<AndroidNdkExtension>.registerIntegrityTask(): TaskProvider<*> {
        return extension.checksums.run {
            checksum(
                checksum = when (host.operatingSystem) {
                    Linux -> linux
                    MacOS -> macos
                    Windows -> windows
                },
                algorithm = Algorithm.SHA1
            )
        }
    }

    override fun ExtractTaskRegistrationScope<AndroidNdkExtension>.registerExtractTask(): TaskProvider<*> {
        return when (host.operatingSystem) {
            Linux, Windows -> unzip(true)
            MacOS -> {
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

    override fun ProjectConfigurationScope<AndroidNdkExtension>.configureProject() {
        if (!supportHost(host)) {
            return
        }

        when (val configurator = configurator) {
            is CProjectConfigurator -> {
                val cParams = createExtension<AndroidNdkCompilationParams>("android").apply {
                    configureConventions(extension.compilationParams)
                }

                val hostTag = when (host.operatingSystem) {
                    Linux -> "linux-x86_64"
                    MacOS -> "darwin-x86_64"
                    Windows -> "windows-x86_64"
                }

                val toolchainDir = installDirectory.map { directory ->
                    directory.dir("toolchains/llvm/prebuilt/$hostTag")
                }

                configurator.registerCompileTask<AndroidNdkCCompileTask>(
                    configure = {
                        this.toolchainDirectory = toolchainDir
                        this.params = cParams
                    },
                    platformFilter = { platform ->
                        platform.operatingSystem == OperatingSystem.Android
                    }
                )
            }
        }
    }
}