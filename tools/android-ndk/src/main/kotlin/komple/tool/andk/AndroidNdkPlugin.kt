package komple.tool.andk

import komple.KompleRootExtensionBase
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

public class AndroidNdkPlugin: KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtensionBase
    ) {
        komple.registerTool("androidNdk", AndroidNdkConfigurator::class)
    }
}