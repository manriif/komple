package komple.gradle

import komple.KOMPLE_EXTENSION_NAME
import komple.KOMPLE_PLUGIN_ID
import komple.gradle.project.configureKmpExtension
import komple.gradle.project.withKmpPlugin
import komple.gradle.exec.DefaultExecService
import komple.gradle.exec.ExecEnvironment
import komple.gradle.extension.KompleRootProjectExtension
import komple.gradle.extension.KompleSubProjectExtension
import komple.gradle.extension.configureConventions
import komple.gradle.extension.configureSubProjectExtension
import komple.gradle.tool.configureTools
import komple.project.registerFactories
import komple.util.getExtensionByName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.registerIfAbsent

/**
 * Komple Gradle plugin.
 */
public class KomplePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (project.parent == null) {
            applyRootProjectExtension(project)
        } else {
            applySubProjectExtension(project)
        }
    }

    /**
     * Applies the root project extension which is meant for Komple tools and projects declaration.
     */
    private fun applyRootProjectExtension(project: Project) {
        val execEnvironment = project.objects.newInstance<ExecEnvironment>()
        val extension = project.extensions.create<KompleRootProjectExtension>(KOMPLE_EXTENSION_NAME)

        extension.run {
            configureConventions()
            extensibleProjects.registerFactories(project)

            execService = project.gradle.sharedServices.registerIfAbsent(
                name = KOMPLE_EXTENSION_NAME,
                implementationType = DefaultExecService::class
            ) {
                parameters {
                    this.interpreter.set(commandInterpreter)
                    this.environment.set(execEnvironment)
                }
            }
        }

        project.configureTools(extension, execEnvironment)
    }

    /**
     * Applies the subproject extension which is meant for Komple registered tools and projects
     * consumption.
     */
    private fun applySubProjectExtension(project: Project) {
        val rootProject = checkNotNull(project.parent)

        rootProject.pluginManager.withPlugin(KOMPLE_PLUGIN_ID) {
            val extension =
                project.extensions.create<KompleSubProjectExtension>(KOMPLE_EXTENSION_NAME)

            val rootExtension =
                rootProject.getExtensionByName<KompleRootProjectExtension>(KOMPLE_EXTENSION_NAME)

            project.configureSubProjectExtension(extension, rootExtension)

            project.withKmpPlugin { kmp ->
                configureKmpExtension(rootExtension, kmp)
            }
        }
    }
}