package komple.gradle.extension

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
public abstract class KompleSubProjectExtension :
    KompleExtension {

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
    root.projectConfiguratorFactories.all {
        configureProjectFromTools(this, extension.projects.extensions, root.configuredTools)
    }

    root.configuredTools.all kTool@{
        extension.tools.extensions.add(KompleTool::class, this@kTool.name.camelCased(), this)
    }

    val sharedCommandExecutors = root.commandExecutors

    extension.run {
        extensions.add(::commandExecutors.name, commandExecutors)
        commandExecutors.addAllLater(provider { sharedCommandExecutors })
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