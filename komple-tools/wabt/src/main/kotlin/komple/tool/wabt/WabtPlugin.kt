package komple.tool.wabt

import komple.KompleRootExtension
import komple.registerTool
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

/**
 * WABT plugin.
 */
public class WabtPlugin : KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool<WabtConfigurator>("WABT")
    }
}