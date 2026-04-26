package komple.gradle.extension

import komple.gradle.project.KompleProjectExtension
import komple.gradle.project.ProjectConfiguratorFactory
import komple.gradle.tool.DefaultKompleTool
import komple.gradle.tool.configureProject
import komple.gradle.util.camelCased
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.create
import javax.inject.Inject

/**
 * Komple extension for subproject.
 * The extension is designed for registered tools and projects consumption.
 */
public abstract class KompleSubProjectExtension @Inject constructor(objects: ObjectFactory) {

    /**
     * Configured projects.
     */
    public val projects: KompleProjectsExtension =
        extensions.create("projects", KompleProjectsExtension::class)

    /**
     * Registered tools.
     */
    public val tools: KompleToolsExtension =
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
        configureProjectFromTools(this, extension.projects.extensions, root.tools)
    }

    root.tools.all kTool@{
        extension.tools.extensions.add(this@kTool.name.camelCased(), this)
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