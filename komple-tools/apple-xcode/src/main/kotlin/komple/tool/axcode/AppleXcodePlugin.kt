package komple.tool.axcode

import komple.KompleRootExtension
import komple.registerTool
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

/**
 * Apple Xcode plugin.
 */
public class AppleXcodePlugin : KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool<AppleXcodeConfigurator>("Apple Xcode")
    }
}