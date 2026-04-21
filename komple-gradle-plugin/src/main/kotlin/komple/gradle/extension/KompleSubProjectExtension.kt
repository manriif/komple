package komple.gradle.extension

import komple.exec.ExecService
import komple.gradle.project.KompleProjectExtension
import komple.gradle.project.ProjectConfiguratorFactory
import komple.gradle.tool.configureProject
import komple.gradle.util.camelCased
import komple.project.ProjectConfigurator
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import javax.inject.Inject

/**
 * Komple extension for subproject.
 * The extension is designed for registered tools and projects consumption.
 */
public abstract class KompleSubProjectExtension @Inject constructor(objects: ObjectFactory) {

    /**
     * Command executor service which can be used to execute command in an environment populated and
     * configured by registered tools.
     */
    public abstract val execService: Property<ExecService>

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
    extension.execService.set(root.execService)

    root.projectConfiguratorFactories .all kProject@{
        val (projectExtension, configurator) = createProjectConfigurator(extension, this)

        root.tools.all {
            configureProject(this@configureSubProjectExtension, projectExtension, configurator)
        }
    }

    root.tools.all kTool@{
        extension.tools.extensions.add(this@kTool.name.camelCased(), this)
    }
}

private fun <Extension : KompleProjectExtension> createProjectConfigurator(
    extension: KompleSubProjectExtension,
    factory: ProjectConfiguratorFactory<Extension>
): Pair<Extension, ProjectConfigurator> {
    val projectExtension = extension.projects.extensions.create(
        factory.kProject.name.camelCased(),
        factory.extensionType,
        factory.kProject
    )

    return projectExtension to factory.createConfigurator(projectExtension)
}