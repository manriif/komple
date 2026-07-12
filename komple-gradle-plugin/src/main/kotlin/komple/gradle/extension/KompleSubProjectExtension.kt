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
package komple.gradle.extension

import komple.exec.ExecEnvironment
import komple.gradle.exec.KompleExecEnvironmentsExtension
import komple.gradle.project.KompleProjectExtension
import komple.gradle.project.KompleProjectsExtension
import komple.gradle.project.ProjectConfiguratorFactory
import komple.gradle.tool.DefaultKompleTool
import komple.gradle.tool.KompleToolsExtension
import komple.gradle.tool.configureProject
import komple.gradle.util.camelCased
import komple.tool.KompleTool
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.create

/**
 * Komple extension for subproject.
 * The extension is designed for registered tools and projects consumption.
 */
public abstract class KompleSubProjectExtension : KompleExtension {

    /**
     * Configured exec environments.
     */
    public val execEnvironments: KompleExecEnvironmentsExtension =
        extensions.create("execEnvironments", KompleExecEnvironmentsExtension::class)

    /**
     * Configured projects.
     */
    public val projects: KompleProjectsExtension =
        extensions.create("projects", KompleProjectsExtension::class)

    /**
     * Registered tools.
     */
    override val tools: KompleToolsExtension =
        extensions.create("tools", KompleToolsExtension::class)
}

///////////////////////////////////////////////////////////////////////////
// Configuration
///////////////////////////////////////////////////////////////////////////

/**
 * Configure `this` extension.
 */
internal fun Project.configureSubProjectExtension(
    extension: KompleSubProjectExtension,
    root: KompleRootProjectExtension
) {
    root.defaultExecEnvironments.all env@{
        extension.execEnvironments.extensions.add(ExecEnvironment::class, this@env.name, this)
    }

    root.projectConfiguratorFactories.all {
        configureProjectFromTools(this, extension.projects.extensions, root.configuredTools)
    }

    root.configuredTools.all kTool@{
        extension.tools.extensions.add(KompleTool::class, this@kTool.name.camelCased(), this)
    }
}

private fun <Extension : KompleProjectExtension> Project.configureProjectFromTools(
    factory: ProjectConfiguratorFactory<Extension>,
    container: ExtensionContainer,
    tools: DomainObjectCollection<DefaultKompleTool<*>>
) {
    val extension = container.create(
        factory.kProject.name.camelCased(),
        factory.extensionType,
        factory.kProject
    )

    tools.all {
        configureProject(
            project = this@configureProjectFromTools,
            projectExtension = extension,
            projectConfigurator = factory.createConfigurator(extension, this)
        )
    }
}