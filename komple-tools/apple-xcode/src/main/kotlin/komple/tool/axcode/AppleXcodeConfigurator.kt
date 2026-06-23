package komple.tool.axcode

import komple.platform.Host
import komple.project.CProjectConfigurator
import komple.project.ProjectConfigurationScope
import komple.project.createExtension
import komple.project.registerCompileTask
import komple.tool.axcode.compile.AppleXcodeCCompileTask
import komple.tool.axcode.compile.AppleXcodeCompilationParams
import komple.tool.axcode.compile.configureConventions
import komple.tool.configurator.DefaultKompleToolConfigurator
import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.createExtension
import org.gradle.kotlin.dsl.assign
import javax.inject.Inject

/**
 * Configurator for Apple Xcode.
 */
public abstract class AppleXcodeConfigurator @Inject constructor(name: String) :
    DefaultKompleToolConfigurator<AppleXcodeExtension>(name) {

    override fun supportHost(host: Host): Boolean = when (host.operatingSystem) {
        MacOS -> true
        Linux, Windows -> false
    }

    override fun ExtensionConfigurationScope<AppleXcodeExtension>.configureExtension(): AppleXcodeExtension {
        return createExtension {
            add(AppleXcodeExtension::compilationParams) {
                extension.configureConventions(project)
            }
        }
    }

    override fun ProjectConfigurationScope<AppleXcodeExtension>.configureProject() {
        if (!supportHost(host)) {
            return
        }

        when (val configurator = configurator) {
            is CProjectConfigurator -> {
                val cParams = createExtension<AppleXcodeCompilationParams>("apple").apply {
                    configureConventions(extension.compilationParams)
                }

                configurator.registerCompileTask<AppleXcodeCCompileTask>(
                    configure = {
                        this.params = cParams
                    },
                    platformFilter = { platform ->
                        platform.operatingSystem is Darwin
                    }
                )
            }
        }
    }
}