package komple.tool.andk

import komple.gradle.KompleBaseExtension
import komple.tool.KompleToolPlugin
import org.gradle.api.Project

public class AndroidNdkPlugin: KompleToolPlugin() {

    override fun configure(
        project: Project,
        komple: KompleBaseExtension
    ) {
        val extension = komple.createExtension(
            name = "androidNdk",
            type = AndroidNdkExtension::class
        )
    }
}