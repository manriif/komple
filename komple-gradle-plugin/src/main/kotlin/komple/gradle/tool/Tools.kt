package komple.gradle.tool

import komple.gradle.Komple
import komple.gradle.exec.ExecEnvironment
import komple.gradle.extension.DefaultExtensionConfigurationScope
import komple.gradle.extension.KompleRootExtension
import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.task.TASK_TOOL_INSTALL_POSTFIX
import komple.gradle.task.outputFiles
import komple.gradle.task.toolTaskName
import komple.gradle.tool.compile.DefaultExecEnvironmentBuilderScope
import komple.gradle.tool.install.DefaultDownloadTaskRegistrationScope
import komple.gradle.tool.install.DefaultExtractTaskRegistrationScope
import komple.gradle.tool.install.DefaultInstallTaskRegistrationScope
import komple.gradle.tool.install.DefaultIntegrityTaskRegistrationScope
import komple.tool.KompleToolConfigurator
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * Configures all tools.
 */
internal fun Project.configureTools(
    extension: KompleRootExtension,
    environment: ExecEnvironment
) {
    val komple = Komple(extension, this)

    extension.toolConfigurators.all {
        configureTool(komple, environment)
    }
}

/**
 * Configures a tool using `this` [KompleToolConfigurator].
 */
private fun KompleToolConfigurator.configureTool(
    komple: Komple,
    environment: ExecEnvironment
) {
    DefaultExtensionConfigurationScope(komple).use { scope ->
        scope.configureExtension()
    }

    val context = KompleToolConfigContext(komple, this.name)
    val installTaskProvider = createInstallTaskProvider(context)

    val installDirectory = komple.project.layout
        .dir(installTaskProvider.outputFiles().map { it.singleFile })

    DefaultExecEnvironmentBuilderScope(
        komple = komple,
        environment = environment,
        installDirectory = installDirectory
    ).use { it.configureEnvironment() }

    val tool = DefaultKompleTool(
        toolName = context.toolName,
        installTaskProvider = installTaskProvider,
        installDirectory = installDirectory
    )

    komple.extension.tools.add(tool)
}

/**
 * Creates the task responsible for installing the tool.
 */
private fun KompleToolConfigurator.createInstallTaskProvider(
    context: KompleToolConfigContext
): TaskProvider<*> = if (supportHost(CurrentHost)) {
    DefaultInstallTaskRegistrationScope(
        context = context,
        extractInputs = DefaultExtractTaskRegistrationScope(
            context = context,
            integrityInputs = DefaultIntegrityTaskRegistrationScope(
                context = context,
                downloadInputs = DefaultDownloadTaskRegistrationScope(context)
                    .use { it.registerDownloadTask() }
                    .outputFiles(context.komple.project.layout)
            ).use { it.registerIntegrityTask() }
                .outputFiles(context.komple.project.layout)
        ).use { it.registerExtractTask() }
            .outputFiles(context.komple.project.layout)
    ).use { it.registerInstallTask() }
} else {
    val unsupportedMessage = "Host is not supported by tool $name"

    context.komple.project.run {
        logger.warn(unsupportedMessage)

        tasks.register(toolTaskName(context.toolName, TASK_TOOL_INSTALL_POSTFIX)) {
            outputs.dir(gradle.kompleToolsInstallsDirectory.dir(context.toolName))
            doLast { throw UnsupportedHostException(unsupportedMessage) }
        }
    }
}