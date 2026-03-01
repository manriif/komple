package komple.gradle

import komple.extension.getExtensionByName
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Identifier of the Kotlin Multiplatform Plugin.
 */
private const val KMP_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"

/**
 * Returns the [KotlinMultiplatformExtension] from `this` project.
 */
public val Project.kmpExtension: KotlinMultiplatformExtension
    get() = getExtensionByName("kotlin")

/**
 * Invokes [action] with the [KotlinMultiplatformExtension] passed as argument when the KMP plugin
 * is applied.
 */
public fun Project.withKmpPlugin(action: Project.(KotlinMultiplatformExtension) -> Unit) {
    pluginManager.withPlugin(KMP_PLUGIN_ID) {
        action(kmpExtension)
    }
}