package komple.tool.zig

import komple.KompleRootExtension
import komple.registerTool
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

/**
 * Zig plugin.
 */
public class ZigPlugin : KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool<ZigConfigurator>("Zig")
    }
}