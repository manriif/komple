/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package komple.gradle.tool

import komple.exec.ShellEnvironment
import komple.gradle.deps.DependencyGraph
import komple.gradle.exec.DefaultExecEnvironment
import komple.gradle.extension.KompleRootProjectExtension
import komple.gradle.kompleToolsCachesDirectory
import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.platform.CurrentHost
import komple.gradle.platform.UnsupportedHostException
import komple.gradle.project.DefaultProjectConfigurationScope
import komple.gradle.project.DefaultShellEnvironmentBuilderScope
import komple.gradle.project.KompleProjectExtension
import komple.gradle.tool.task.DefaultDownloadTaskRegistrationScope
import komple.gradle.tool.task.DefaultExtractTaskRegistrationScope
import komple.gradle.tool.task.DefaultInstallTaskRegistrationScope
import komple.gradle.tool.task.DefaultIntegrityTaskRegistrationScope
import komple.gradle.tool.task.TASK_TOOL_INSTALL_POSTFIX
import komple.gradle.tool.task.toolTaskName
import komple.gradle.util.camelCased
import komple.gradle.util.dashCased
import komple.project.ProjectConfigurator
import komple.tool.configurator.KompleToolConfigurator
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.newInstance

/**
 * Configures all tools.
 */
internal fun Project.configureTools(extension: KompleRootProjectExtension) {
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
                addEnvironment(dependency.shellEnvironment)

                installTaskProvider.configure {
                    dependsOn(dependency.installTaskProvider)
                }
            }
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

    // Environment that can be used during tool installation only
    val installExecEnvironment = objects.newInstance<DefaultExecEnvironment>(toolName).apply {
        commandInterpreter = rootExtension.commandInterpreter
    }

    val context = KompleToolConfigContext(project, toolName, extension) {
        installExecEnvironment
    }

    val cacheDirectory = project.gradle.kompleToolsCachesDirectory.dir(toolName.dashCased())
    val cacheDirectoryProvider = project.providers.provider { cacheDirectory }
    val installTaskProvider = createInstallTaskProvider(context, cacheDirectoryProvider)

    val installDirectory = project.layout
        .dir(installTaskProvider.map { it.outputs.files.singleFile })

    val shellEnvironment = objects.newInstance<ShellEnvironment>()

    if (supportHost(CurrentHost)) {
        DefaultShellEnvironmentBuilderScope(
            context = context,
            environment = shellEnvironment,
            cacheDirectory = cacheDirectoryProvider,
            installDirectory = installDirectory
        ).use { it.configureEnvironment() }
    }

    val tool = DefaultKompleTool(
        objects = objects,
        configurator = this,
        extension = context.extension,
        toolName = context.toolName,
        dependencyGraph = dependencyGraph,
        installExecEnvironment = installExecEnvironment,
        shellEnvironment = shellEnvironment,
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
    cacheDirectory: Provider<Directory>
): TaskProvider<*> = if (supportHost(CurrentHost)) {
    DefaultInstallTaskRegistrationScope(
        context = context,
        cacheDirectory = cacheDirectory,
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

/**
 * Makes `this` tool configure the project.
 */
internal fun <Ext : KompleToolExtension> DefaultKompleTool<Ext>.configureProject(
    project: Project,
    projectExtension: KompleProjectExtension,
    projectConfigurator: ProjectConfigurator
) {
    val context =
        KompleToolConfigContext(project, toolName, extension, usageExecEnvironmentProvider)

    val scope = DefaultProjectConfigurationScope(
        context = context,
        projectExtension = projectExtension,
        configurator = projectConfigurator,
        installDirectory = installDirectory,
    )

    configurator.run {
        scope.use { it.configureProject() }
    }
}