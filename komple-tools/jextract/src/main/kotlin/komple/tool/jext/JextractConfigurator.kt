package komple.tool.jext

import komple.exec.ExecEnvironmentBuilderScope
import komple.exec.path
import komple.platform.Host
import komple.project.CProjectConfigurator
import komple.project.ProjectConfigurationScope
import komple.project.createExtension
import komple.project.registerExecTask
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
        MacOS, Linux -> true
        Windows -> when (host.architecture) {
            Arm64 -> false
            X64 -> true
        }
    }

    override fun ExtensionConfigurationScope<JextractExtension>.configureExtension(): JextractExtension {
        return createExtension {
            extension.run {
                version.convention(kompleProperty("jextract.version"))
                jdkVersion.convention(JavaVersion.toVersion(kompleProperty("jextract.jdkVersion")))

                add(JextractExtension::checksums) {
                    extension.run {
                        linuxAarch64 = kompleProperty("jextract.checksum.linux.aarch64")
                        linuxX64 = kompleProperty("jextract.checksum.linux.x64")
                        macosAarch64 = kompleProperty("jextract.checksum.macos.aarch64")
                        macosX64 = kompleProperty("jextract.checksum.macos.x64")
                        windowsX64 = kompleProperty("jextract.checksum.windows.x64")
                    }
                }
            }
        }
    }

    override fun DownloadTaskRegistrationScope<JextractExtension>.registerDownloadTask(): TaskProvider<*> {
        val platform = when (host.operatingSystem) {
            Linux -> when (host.architecture) {
                Arm64 -> "linux-aarch64"
                X64 -> "linux-x64"
            }

            MacOS -> when (host.architecture) {
                Arm64 -> "macos-aarch64"
                X64 -> "macos-x64"
            }

            Windows -> "windows-x64"
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
                    Linux -> when (host.architecture) {
                        Arm64 -> linuxAarch64
                        X64 -> linuxX64
                    }

                    MacOS -> when (host.architecture) {
                        Arm64 -> macosAarch64
                        X64 -> macosX64
                    }

                    Windows -> windowsX64
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

    override fun ProjectConfigurationScope<JextractExtension>.configureProject() {
        when (val configurator = configurator) {
            is CProjectConfigurator -> createExtension<JextractCProjectExtension>("jextract") {
                add(JextractCProjectExtension::bindingGenerators)
                val layout = project.layout

                extension.extensibleBindingGenerators.registerFactory(
                    JextractBindingGenerator::class.java
                ) { name ->
                    val options = project.objects.newInstance<JextractCommandLineOptions>()

                    val taskProvider = registerExecTask<JextractGenerateBindingsTask>(
                        postfix = "generateBindings${name}"
                    ) {
                        cProject = configurator.project
                        cliOptions = options

                        outputDirectory = generatedDirectory().map { directory ->
                            directory.dir("jextract-${name}")
                        }
                    }

                    val generateDirectory = layout
                        .dir(taskProvider.map { it.outputs.files.singleFile })

                    JextractBindingGeneratorImpl(
                        generatorName = name,
                        options = options,
                        cProject = configurator.project,
                        generateTaskProvider = taskProvider,
                        generateDirectory = generateDirectory
                    )
                }
            }
        }
    }
}