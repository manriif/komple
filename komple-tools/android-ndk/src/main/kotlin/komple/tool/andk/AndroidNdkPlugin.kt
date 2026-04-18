package komple.tool.andk

import komple.KompleRootExtension
import komple.registerTool
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

/**
 * Android NDK plugin.
 */
public class AndroidNdkPlugin: KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool<AndroidNdkConfigurator>("Android NDK")
    }
}