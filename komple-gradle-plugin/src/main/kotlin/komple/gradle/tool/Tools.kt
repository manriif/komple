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
@file:Suppress("UnstableApiUsage")

package komple.gradle.tool

import komple.exec.ShellEnvironment
import komple.gradle.deps.DependencyGraph
import komple.gradle.exec.DefaultExecEnvironment
import komple.gradle.extension.KompleRootProjectExtension
import komple.gradle.kompleToolsCachesDirectory
import komple.gradle.kompleToolsInstallsDirectory
import komple.gradle.platform.configureUnsupportedHost
import komple.gradle.problem.KompleHostUnsupportedProblemId
import komple.gradle.problem.ProblemThrowerTask
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
import komple.platform.Host
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
import org.gradle.kotlin.dsl.register

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
    val objects = project.objects
    val host = rootExtension.host

    if (!supportHost(host)) {
        rootExtension.problems.reporter.report(KompleHostUnsupportedProblemId) {
            details("The tool $toolName does not support running on $host")
            solution("Run the build on a host that is supported by the tool $toolName")
        }
    }

    val extension = DefaultExtensionConfigurationScope<Ext>(
        project = project,
        rootExtension = rootExtension,
        toolName = toolName
    ).use { it.configureExtension() }

    // Environment that can be used during tool installation only
    val installExecEnvironment = objects.newInstance<DefaultExecEnvironment>(toolName).apply {
        commandInterpreter = rootExtension.commandInterpreter
    }

    val context = KompleToolConfigContext(project, host, toolName, extension) {
        installExecEnvironment
    }

    val cacheDirectory = project.gradle.kompleToolsCachesDirectory.dir(toolName.dashCased())
    val cacheDirectoryProvider = project.providers.provider { cacheDirectory }
    val allTaskProviders = registerToolTaskProviders(context, cacheDirectoryProvider)
    val installTaskProvider = allTaskProviders.last()

    val installDirectory = project.layout
        .dir(installTaskProvider.map { it.outputs.files.singleFile })

    val shellEnvironment = objects.newInstance<ShellEnvironment>()

    if (supportHost(host)) {
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
        allTaskProviders = allTaskProviders,
        installTaskProvider = installTaskProvider,
        installDirectory = installDirectory
    )

    rootExtension.configuredTools.add(tool)
}

/**
 * Registers and returns the tasks responsible for downloading, checking, extracting to installing
 * the tool, in that order. All tasks other than installing are optional.
 */
private fun <Ext : KompleToolExtension> KompleToolConfigurator<Ext>.registerToolTaskProviders(
    context: KompleToolConfigContext<Ext>,
    cacheDirectory: Provider<Directory>
): List<TaskProvider<*>> = if (supportHost(context.host)) {
    val download = DefaultDownloadTaskRegistrationScope(context)
        .use { it.registerDownloadTask() }

    val integrity = DefaultIntegrityTaskRegistrationScope(context, download)
        .use { it.registerIntegrityTask() }

    val extract = DefaultExtractTaskRegistrationScope(context, integrity)
        .use { it.registerExtractTask() }

    val install = DefaultInstallTaskRegistrationScope(
        context = context,
        cacheDirectory = cacheDirectory,
        extractTask = extract
    ).use { it.registerInstallTask() }

    listOf(download, integrity, extract, install)
} else {
    val install = context.project.run {
        tasks.register<ProblemThrowerTask>(
            toolTaskName(
                context.toolName,
                TASK_TOOL_INSTALL_POSTFIX
            )
        ) {
            outputs.dir(gradle.kompleToolsInstallsDirectory.dir(context.toolName))
            configureUnsupportedHost(context.toolName, context.host)
        }
    }

    listOf(install)
}

/**
 * Makes `this` tool configure the project.
 */
internal fun <Ext : KompleToolExtension> DefaultKompleTool<Ext>.configureProject(
    project: Project,
    host: Host,
    projectExtension: KompleProjectExtension,
    projectConfigurator: ProjectConfigurator
) {
    val context =
        KompleToolConfigContext(project, host, toolName, extension, usageExecEnvironmentProvider)

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