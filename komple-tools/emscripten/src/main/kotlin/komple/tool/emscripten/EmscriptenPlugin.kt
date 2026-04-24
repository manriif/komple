package komple.tool.emscripten

import komple.KompleRootExtension
import komple.registerTool
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

/**
 * Emscripten plugin.
 */
public class EmscriptenPlugin : KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool<EmscriptenConfigurator>("Emscripten")
    }
}