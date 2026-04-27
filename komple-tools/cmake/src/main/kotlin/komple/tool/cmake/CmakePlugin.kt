package komple.tool.cmake

import komple.KompleRootExtension
import komple.registerTool
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

/**
 * CMake plugin.
 */
public class CmakePlugin : KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool<CmakeConfigurator>("CMake")
    }
}