package komple.gradle

import komple.KOMPLE_EXTENSION_NAME
import komple.gradle.exec.ExecEnvironment
import komple.gradle.exec.DefaultExecService
import komple.gradle.extension.KompleRootExtension
import komple.gradle.tool.configureTools
import komple.project.registerFactories
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.registerIfAbsent

/**
 * Komple plugin that applies to root project.
 */
public class KompleRootPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        check(project.parent == null) {
            "Komple Root plugin only applies to the root project"
        }

        val extension = project.extensions.create<KompleRootExtension>(KOMPLE_EXTENSION_NAME)
        val environment = project.objects.newInstance<ExecEnvironment>()

        val execService = project.gradle.sharedServices.registerIfAbsent(
            name = KOMPLE_EXTENSION_NAME,
            implementationType = DefaultExecService::class
        ) {
            parameters {
                this.environment.set(environment)
            }
        }

        extension.projects.registerFactories(project)
        project.configureTools(extension, environment)
    }
}