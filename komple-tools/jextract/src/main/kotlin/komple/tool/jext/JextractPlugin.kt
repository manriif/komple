package komple.tool.jext

import komple.KompleRootExtension
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

public class JextractPlugin : KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleRootExtension
    ) {
        komple.registerTool("jextract", JextractConfigurator::class)
    }
}