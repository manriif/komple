package komple.gradle.extension

import komple.exec.ExecService
import komple.gradle.project.c.KompleCProjectExtension
import komple.gradle.tool.configureProject
import komple.gradle.util.camelCased
import komple.project.KompleCProject
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

    root.projects.all kProject@{
        val extensionType = when (this@kProject) {
            is KompleCProject -> KompleCProjectExtension::class
        }

        val projectExtension = extension.projects.extensions.create(
            this@kProject.name.camelCased(),
            extensionType,
            this@configureSubProjectExtension,
            this@kProject
        )

        root.tools.all {
            configureProject(this@configureSubProjectExtension, this@kProject, projectExtension)
        }
    }

    root.tools.all kTool@{
        extension.tools.extensions.add(this@kTool.name.camelCased(), this)
    }
}