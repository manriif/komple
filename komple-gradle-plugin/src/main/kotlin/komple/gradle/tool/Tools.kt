package komple.gradle.tool

import komple.gradle.KompleExtension
import komple.gradle.extension.DefaultExtensionConfigurationScope
import komple.gradle.Komple
import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.task.DefaultInputs
import komple.gradle.task.DefaultDownloadTaskRegistrationScope
import komple.gradle.task.DefaultExtractTaskRegistrationScope
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

    val installTaskName = toolTaskName(name, "Install")
    val installDirectory = komple.project.gradle.kompleToolsInstallsDirectory.dir(name)

    if (!supportHost(CurrentHost)) {
        val unsupportedMessage = "Host is not supported by tool $name"
        komple.project.logger.warn(unsupportedMessage)

        val tool = DefaultKompleTool(
            toolName = name,
            installTaskProvider = komple.project.tasks.register(installTaskName) {
                doLast { throw UnsupportedHostException(unsupportedMessage) }
            },
            installDirectory = installDirectory
        )

        return komple.addTool(tool)
    }

    val downloadTaskProvider = DefaultDownloadTaskRegistrationScope(komple, name).use { scope ->
        scope.registerDownloadTask()
    }

    val integrityTaskProvider = DefaultIntegrityTaskRegistrationScope(
        komple = komple,
        toolName = name,
        downloadInputs = DefaultInputs(
            files = downloadTaskProvider.outputFiles(),
            layout = komple.project.layout
        )
    ).use { scope ->
        scope.registerIntegrityTask()
    }

    val extractTaskProvider = DefaultExtractTaskRegistrationScope(
        komple = komple,
        toolName = name,
        integrityInputs = DefaultInputs(
            files = integrityTaskProvider.outputFiles(),
            layout = komple.project.layout
        )
    ).use { scope ->
        scope.registerExtractTask()
    }
}

private fun Komple.addTool(tool: DefaultKompleTool) {
    extension.tools.apply {
        add(tool)
        //extensions.add(tool.name, tool)
    }
}