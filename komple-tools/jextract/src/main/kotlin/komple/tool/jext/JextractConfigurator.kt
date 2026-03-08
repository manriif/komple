package komple.tool.jext

import komple.exec.path
import komple.platform.Architecture
import komple.platform.Host
import komple.platform.OperatingSystem
import komple.project.KompleCProject
import komple.tool.compile.CompilationBuilderScope
import komple.tool.compile.ExecEnvironmentBuilderScope
import komple.tool.compile.createExtension
import komple.tool.compile.registerTask
import komple.tool.configurator.DefaultKompleToolConfigurator
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.createExtension
import komple.tool.extension.kompleProperty
import komple.tool.jext.generator.JextractBindingGenerator
import komple.tool.jext.generator.JextractBindingGeneratorImpl
import komple.tool.jext.generator.JextractCommandLineOptions
import komple.tool.jext.generator.JextractGenerateBindingsTask
import komple.tool.task.Algorithm
import komple.tool.task.DownloadTaskRegistrationScope
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.IntegrityTaskRegistrationScope
import komple.tool.task.checksum
import komple.tool.task.untarGzip
import komple.tool.task.url
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/**
 * Configurator for Jextract.
 */
public abstract class JextractConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<JextractExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        OperatingSystem.MacOS, OperatingSystem.Linux -> true
        OperatingSystem.Windows -> when (host.architecture) {
            Architecture.Arm64 -> false
            Architecture.X64 -> true
        }
    }

    override fun ExtensionConfigurationScope<JextractExtension>.configureExtension(): JextractExtension {
        return createExtension {
            extension.run {
                version.convention(kompleProperty("jextract.version"))
                jdkVersion.convention(JavaVersion.toVersion(kompleProperty("jextract.version")))

                checksums.convention(
                    JextractChecksums(
                        linuxAarch64 = kompleProperty("jextract.checksum.linux.aarch64"),
                        linuxX64 = kompleProperty("jextract.checksum.linux.x64"),
                        macosAarch64 = kompleProperty("jextract.checksum.macos.aarch64"),
                        macosX64 = kompleProperty("jextract.checksum.macos.x64"),
                        windowsX64 = kompleProperty("jextract.checksum.windows.x64"),
                    )
                )
            }
        }
    }

    override fun DownloadTaskRegistrationScope<JextractExtension>.registerDownloadTask(): TaskProvider<*> {
        val platform = when (host.operatingSystem) {
            OperatingSystem.Linux -> when (host.architecture) {
                Architecture.Arm64 -> "linux-aarch64"
                Architecture.X64 -> "linux-x64"
            }

            OperatingSystem.MacOS -> when (host.architecture) {
                Architecture.Arm64 -> "macos-aarch64"
                Architecture.X64 -> "macos-x64"
            }

            OperatingSystem.Windows -> "windows-x64"
        }

        return url(extension.version.zip(extension.jdkVersion) { jextractVersion, javaVersion ->
            val major = jextractVersion.substringBefore('-')
            val jdkVersion = javaVersion.majorVersion
            val file = "openjdk-$jdkVersion-jextract+${jextractVersion}_${platform}_bin.tar.gz"
            "https://download.java.net/java/early_access/jextract/$jdkVersion/$major/$file"
        })
    }

    override fun IntegrityTaskRegistrationScope<JextractExtension>.registerIntegrityTask(): TaskProvider<*> {
        return extension.checksums.run {
            checksum(
                checksum = when (host.operatingSystem) {
                    OperatingSystem.Linux -> when (host.architecture) {
                        Architecture.Arm64 -> map(JextractChecksums::linuxAarch64)
                        Architecture.X64 -> map(JextractChecksums::linuxX64)
                    }

                    OperatingSystem.MacOS -> when (host.architecture) {
                        Architecture.Arm64 -> map(JextractChecksums::macosAarch64)
                        Architecture.X64 -> map(JextractChecksums::macosX64)
                    }

                    OperatingSystem.Windows -> map(JextractChecksums::windowsX64)
                },
                algorithm = Algorithm.SHA_256
            )
        }
    }

    override fun ExtractTaskRegistrationScope<JextractExtension>.registerExtractTask(): TaskProvider<*> {
        return untarGzip(true)
    }

    override fun ExecEnvironmentBuilderScope<JextractExtension>.configureEnvironment() {
        path(installDirectory.map { it.dir("bin") })
    }

    override fun CompilationBuilderScope<JextractExtension>.configureCompilation() {
        when (val kompleProject = project) {
            is KompleCProject -> createExtension<JextractCompilationExtension>("jextract") {
                extension.extensibleGenerateBindingsTasks.registerFactory(
                    JextractBindingGenerator::class.java
                ) { name ->
                    val options = project.objects.newInstance<JextractCommandLineOptions>()

                    val task = registerTask<JextractGenerateBindingsTask>(
                        postfix = "jextractGenerateBindings${name}"
                    ) {
                        this.cProject = kompleProject
                        this.cliOptions = options
                        this.outputDirectory = generatedDirectory("jextract-${name}")
                    }

                    project.objects.newInstance<JextractBindingGeneratorImpl>(options, task)
                }
            }
        }
    }
}