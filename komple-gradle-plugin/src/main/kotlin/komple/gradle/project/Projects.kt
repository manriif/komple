package komple.gradle.project

import komple.gradle.extension.extensions
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

/**
 * Configures the Kotlin [target] for given Komple project [kProject].
 */
internal fun configureKotlinTargetForKompleProject(
    kProject: KompleProject,
    target: KotlinTarget
) {
    when (kProject) {
        is KompleCProject -> configureKotlinTargetForKompleCProject(kProject, target)
    }
}

/**
 * Configures the Kotlin [target] for given Komple C project [kProject].
 */
private fun configureKotlinTargetForKompleCProject(
    kProject: KompleCProject,
    target: KotlinTarget
) {
    kProject.extensions.getByName()
}