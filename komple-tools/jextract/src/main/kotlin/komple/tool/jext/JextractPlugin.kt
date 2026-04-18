package komple.tool.jext

import komple.KompleRootExtension
import komple.registerTool
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

/**
 * Jextract plugin.
 */
public class JextractPlugin : KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool<JextractConfigurator>("Jextract")
    }
}