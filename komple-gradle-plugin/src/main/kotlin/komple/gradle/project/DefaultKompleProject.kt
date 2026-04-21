@file:Suppress("UnstableApiUsage")

package komple.gradle.project

import komple.project.KompleProject
import org.gradle.api.Project
import org.gradle.api.tasks.Internal

/**
 * Sealed set of Komple projects.
 */
internal abstract class DefaultKompleProject(@Internal private val projectName: String) :
    KompleProject {

    override fun getName(): String {
        return projectName
    }
}

/**
 * Configures the conventions values.
 */
internal fun KompleProject.configureCommonConventions(project: Project) {
    packageName.convention(project.provider { "No package name was provided" })
    sourceFiles.convention(project.provider { error("No source files were provided") })
}