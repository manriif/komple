package komple.gradle

import komple.KOMPLE_EXTENSION_NAME
import komple.KOMPLE_ROOT_PLUGIN_ID
import komple.extension.getExtensionByName
import komple.gradle.extension.KompleExtension
import komple.gradle.extension.KompleRootExtension
import komple.gradle.extension.configureExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

/**
 * Komple plugin that applies to subproject.
 */
public class KomplePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val rootProject = checkNotNull(project.parent) {
            "Komple plugin only applies to non-root project"
        }

        rootProject.pluginManager.withPlugin(KOMPLE_ROOT_PLUGIN_ID) {
            val extension = project.extensions.create<KompleExtension>(KOMPLE_EXTENSION_NAME)

            val rootExtensions =
                rootProject.getExtensionByName<KompleRootExtension>(KOMPLE_EXTENSION_NAME)

            configureExtension(
                extension = extension,
                rootExtension = rootExtensions
            )
        }
    }
}