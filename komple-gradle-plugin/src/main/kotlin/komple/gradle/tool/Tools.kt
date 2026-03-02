package komple.gradle.tool

import komple.gradle.Komple
import komple.gradle.KompleExtension
import komple.gradle.extension.DefaultExtensionConfigurationScope
import komple.gradle.extension.extensions
import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.task.DefaultDownloadTaskRegistrationScope
import komple.gradle.task.DefaultExtractTaskRegistrationScope
import komple.gradle.task.DefaultInstallTaskRegistrationScope
import komple.gradle.task.DefaultIntegrityTaskRegistrationScope
import komple.gradle.task.outputFiles
import komple.gradle.task.toolTaskName
import komple.tool.KompleToolConfigurator
import org.gradle.api.Project

/**
 * Configures all tools.
 */
internal fun Project.configureTools(extension: KompleExtension) {
    val komple = Komple(extension, this)

    extension.toolConfigurators.configureEach {
        configureTool(komple)
    }
}

/**
 * Configures a tool using `this` [KompleToolConfigurator].
 */
private fun KompleToolConfigurator.configureTool(komple: Komple) {
    DefaultExtensionConfigurationScope(komple).use { scope ->
        scope.configureExtension()
    }

    val toolName = this.name
    val context = KompleToolConfigContext(komple, toolName)
    val installTaskName = toolTaskName(toolName, "Install")
    val layout = komple.project.layout

    val installTaskProvider = if (supportHost(CurrentHost)) {
        DefaultInstallTaskRegistrationScope(
            context = context,
            taskName = installTaskName,
            extractInputs = DefaultExtractTaskRegistrationScope(
                context = context,
                integrityInputs = DefaultIntegrityTaskRegistrationScope(
                    context = context,
                    downloadInputs = DefaultDownloadTaskRegistrationScope(context)
                        .use { it.registerDownloadTask() }
                        .outputFiles(layout)
                ).use { it.registerIntegrityTask() }
                    .outputFiles(layout)
            ).use { it.registerExtractTask() }
                .outputFiles(layout)
        ).use { it.registerInstallTask() }
    } else {
        val unsupportedMessage = "Host is not supported by tool $name"

        komple.project.run {
            logger.warn(unsupportedMessage)

            tasks.register(installTaskName) {
                outputs.dir( gradle.kompleToolsInstallsDirectory.dir(toolName))
                doLast { throw UnsupportedHostException(unsupportedMessage) }
            }
        }
    }

    val tool = DefaultKompleTool(
        toolName = toolName,
        installTaskProvider = installTaskProvider,
        installDirectory = layout.dir(installTaskProvider.outputFiles().map { it.singleFile })
    )

    komple.extension.tools.extensions.add(tool.name, tool)
}