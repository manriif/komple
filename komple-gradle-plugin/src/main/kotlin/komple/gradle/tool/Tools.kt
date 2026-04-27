package komple.gradle.tool

import komple.exec.CommandExecutor
import komple.exec.ExecEnvironment
import komple.gradle.KomplePlugin
import komple.gradle.deps.DependencyGraph
import komple.gradle.exec.DefaultCommandExecutor
import komple.gradle.extension.KompleRootProjectExtension
import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.project.DefaultExecEnvironmentBuilderScope
import komple.gradle.project.DefaultProjectConfigurationScope
import komple.gradle.project.KompleProjectExtension
import komple.gradle.tool.task.DefaultDownloadTaskRegistrationScope
import komple.gradle.tool.task.DefaultExtractTaskRegistrationScope
import komple.gradle.tool.task.DefaultInstallTaskRegistrationScope
import komple.gradle.tool.task.DefaultIntegrityTaskRegistrationScope
import komple.gradle.tool.task.TASK_TOOL_INSTALL_POSTFIX
import komple.gradle.tool.task.toolTaskName
import komple.gradle.util.camelCased
import komple.project.ProjectConfigurator
import komple.tool.configurator.KompleToolConfigurator
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.resources.MissingResourceException
import org.gradle.api.tasks.TaskProvider
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.newInstance
import java.util.*

private const val TOOLS_PROPERTIES = "tools.properties"

/**
 * Configures all tools.
 */
internal fun Project.configureTools(extension: KompleRootProjectExtension) {
    loadToolsProperties()

    val dependencyGraph = DependencyGraph<RootKompleTool>()

    extension.toolConfigurators.all {
        configureTool(
            project = this@configureTools,
            rootExtension = extension,
            dependencyGraph = dependencyGraph
        )
    }

    extension.configuredTools.all kTool@{
        extension.tools.extensions.add(RootKompleTool::class, this@kTool.name.camelCased(), this)
    }

    afterEvaluate {
        extension.configuredTools.all {
            val dependencies = dependencyGraph.getDependencies(this)

            dependencies.forEach { dependency ->
                execEnvironments.add(dependency.execEnvironment)
            }
        }
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
    dependencyGraph: DependencyGraph<RootKompleTool>
) {
    val toolName = this.name

    val extension = DefaultExtensionConfigurationScope<Ext>(
        project = project,
        rootExtension = rootExtension,
        toolName = toolName
    ).use { it.configureExtension() }

    val objects = project.objects
    val execEnvironment = objects.newInstance<ExecEnvironment>()
    val execEnvironments = mutableListOf(execEnvironment)
    val commandInterpreter = rootExtension.commandInterpreter

    val commandExecutor: Provider<CommandExecutor> = project.providers.provider {
        objects.newInstance<DefaultCommandExecutor>().apply {
            this.commandInterpreter = commandInterpreter
            this.execEnvironments = execEnvironments
        }
    }

    val context = KompleToolConfigContext(project, toolName, extension)
    val installTaskProvider = createInstallTaskProvider(context, commandExecutor)

    val installDirectory = project.layout
        .dir(installTaskProvider.map { it.outputs.files.singleFile })

    if (supportHost(CurrentHost)) {
        DefaultExecEnvironmentBuilderScope(context, execEnvironment, installDirectory)
            .use { it.configureEnvironment() }
    }

    val tool = DefaultKompleTool(
        configurator = this,
        extension = context.extension,
        toolName = context.toolName,
        dependencyGraph = dependencyGraph,
        execEnvironments = execEnvironments,
        commandExecutor = commandExecutor,
        execEnvironment = execEnvironment,
        installTaskProvider = installTaskProvider,
        installDirectory = installDirectory
    )

    rootExtension.configuredTools.add(tool)
}

/**
 * Creates the task responsible for installing the tool.
 */
private fun <Ext : KompleToolExtension> KompleToolConfigurator<Ext>.createInstallTaskProvider(
    context: KompleToolConfigContext<Ext>,
    commandExecutor: Provider<CommandExecutor>,
): TaskProvider<*> = if (supportHost(CurrentHost)) {
    DefaultInstallTaskRegistrationScope(
        context = context,
        commandExecutor = commandExecutor,
        extractTask = DefaultExtractTaskRegistrationScope(
            context = context,
            commandExecutor = commandExecutor,
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

/**
 * Makes `this` tool configure the project.
 */
internal fun <Ext : KompleToolExtension> DefaultKompleTool<Ext>.configureProject(
    project: Project,
    projectExtension: KompleProjectExtension,
    projectConfigurator: ProjectConfigurator
) {
    val context = KompleToolConfigContext(project, toolName, extension)

    val scope = DefaultProjectConfigurationScope(
        context = context,
        projectExtension = projectExtension,
        configurator = projectConfigurator,
        installDirectory = installDirectory,
        commandExecutor = commandExecutor,
    )

    configurator.run {
        scope.use { it.configureProject() }
    }
}