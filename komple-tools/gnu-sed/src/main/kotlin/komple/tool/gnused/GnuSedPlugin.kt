package komple.tool.gnused

import komple.KompleRootExtension
import komple.registerTool
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

/**
 * GNU sed plugin.
 */
public class GnuSedPlugin : KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool<GnuSedConfigurator>("GNU sed")
    }
}