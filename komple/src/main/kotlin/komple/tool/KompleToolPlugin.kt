package komple.tool

import komple.gradle.KOMPLE_PLUGIN_ID
import komple.gradle.KompleBaseExtension
import komple.gradle.kompleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class KompleToolPlugin : Plugin<Project> {

    /**
     * Applies the plugin only if Komple is applied.
     */
    final override fun apply(target: Project) {
        target.pluginManager.withPlugin(KOMPLE_PLUGIN_ID) {
            configure(target, target.kompleExtension)
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
        komple: KompleBaseExtension
    )
}