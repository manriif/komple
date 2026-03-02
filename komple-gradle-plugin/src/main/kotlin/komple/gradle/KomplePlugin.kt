package komple.gradle

import komple.gradle.tool.configureTools
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

/**
 * Komple project contributor.
 */
class KomplePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<KompleExtension>(KOMPLE_EXTENSION_NAME).apply {
            registerNestedExtensions()
        }

        project.configureTools(extension)
    }
}