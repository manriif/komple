package komple.tool

import komple.KOMPLE_PLUGIN_ID
import komple.KompleRootExtensionBase
import komple.kompleRootExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class KompleToolPlugin : Plugin<Project> {

    /**
     * Applies the plugin only if Komple was applied.
     */
    final override fun apply(project: Project) {
        check(project.parent == null) {
            "Komple tool plugin must be applied on root project"
        }

        project.pluginManager.withPlugin(KOMPLE_PLUGIN_ID) {
            configure(project, project.kompleRootExtension)
        }
    }

    /**
     * Applies `this` plugin on [project].
     *
     * It is guaranteed that the komple plugin was applied.
     * It is not guaranteed that the kmp plugin was applied.
     */
    protected abstract fun configure(
        project: Project,
        komple: KompleRootExtensionBase
    )
}