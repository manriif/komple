package komple.gradle.tool

import komple.gradle.KomplePlugin
import komple.gradle.exec.ExecEnvironment
import komple.gradle.extension.KompleRootProjectExtension
import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.task.TASK_TOOL_INSTALL_POSTFIX
import komple.gradle.task.toolTaskName
import komple.gradle.tool.compile.DefaultExecEnvironmentBuilderScope
import komple.gradle.tool.extension.DefaultExtensionConfigurationScope
import komple.gradle.tool.task.DefaultDownloadTaskRegistrationScope
import komple.gradle.tool.task.DefaultExtractTaskRegistrationScope
import komple.gradle.tool.task.DefaultInstallTaskRegistrationScope
import komple.gradle.tool.task.DefaultIntegrityTaskRegistrationScope
import komple.tool.configurator.KompleToolConfigurator
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Project
import org.gradle.api.resources.MissingResourceException
import org.gradle.api.tasks.TaskProvider
import org.gradle.internal.extensions.core.extra
import java.util.Properties

private const val TOOLS_PROPERTIES = "tools.properties"

/**
 * Configures all tools.
 */
internal fun Project.configureTools(
    extension: KompleRootProjectExtension,
    environment: ExecEnvironment
) {
    loadToolsProperties()

    extension.toolConfigurators.all {
        configureTool(this@configureTools, extension, environment)
    }
}

/**
 * Injects [TOOLS_PROPERTIES] into project.
 */
private fun Project.loadToolsProperties() {
    val resource = try {
        KomplePlugin::class.java.classLoader.getResource(TOOLS_PROPERTIES)
    } catch (cause: Throwable) {
        throw MissingResourceException("File $TOOLS_PROPERTIES is missing", cause)
    }

    val properties = Properties().apply {
        load(resource.openStream())
    }

    properties.forEach { (key, value) ->
        val name = key.toString()

        if (!extra.has(name)) {
            extra.set(name, value?.toString())
        }
    }
}

/**
 * Configures a tool using `this` [KompleToolConfigurator].
 */
private fun <Ext : KompleToolExtension> KompleToolConfigurator<Ext>.configureTool(
    project: Project,
    rootExtension: KompleRootProjectExtension,
    environment: ExecEnvironment
) {
    val toolName = this.name

    val extension = DefaultExtensionConfigurationScope<Ext>(
        project = project,
        rootExtension = rootExtension,
        toolName = toolName
    ).use { it.configureExtension() }

    val context = KompleToolConfigContext(project, rootExtension, toolName, extension)
    val installTaskProvider = createInstallTaskProvider(context)

    val installDirectory = project.layout
        .dir(installTaskProvider.map { it.outputs.files.singleFile })

    DefaultExecEnvironmentBuilderScope(context, environment, installDirectory)
        .use { it.configureEnvironment() }

    val tool = DefaultKompleTool(
        toolName = context.toolName,
        installTaskProvider = installTaskProvider,
        installDirectory = installDirectory
    )

    rootExtension.tools.add(tool)
}

/**
 * Creates the task responsible for installing the tool.
 */
private fun <Ext : KompleToolExtension> KompleToolConfigurator<Ext>.createInstallTaskProvider(
    context: KompleToolConfigContext<Ext>
): TaskProvider<*> = if (supportHost(CurrentHost)) {
    DefaultInstallTaskRegistrationScope(
        context = context,
        extractTask = DefaultExtractTaskRegistrationScope(
            context = context,
            integrityTask = DefaultIntegrityTaskRegistrationScope(
                context = context,
                downloadTask = DefaultDownloadTaskRegistrationScope(context)
                    .use { it.registerDownloadTask() }
            ).use { it.registerIntegrityTask() }
        ).use { it.registerExtractTask() }
    ).use { it.registerInstallTask() }
} else {
    val unsupportedMessage = "Host is not supported by tool $name"

    context.project.run {
        logger.warn(unsupportedMessage)

        tasks.register(toolTaskName(context.toolName, TASK_TOOL_INSTALL_POSTFIX)) {
            outputs.dir(gradle.kompleToolsInstallsDirectory.dir(context.toolName))
            doLast { throw UnsupportedHostException(unsupportedMessage) }
        }
    }
}