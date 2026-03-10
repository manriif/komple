package komple.tool.andk

import komple.KompleRootExtension
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

public class AndroidNdkPlugin: KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool("Android NDK", AndroidNdkConfigurator::class)
    }
}