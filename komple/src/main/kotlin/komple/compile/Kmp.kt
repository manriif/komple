package komple.compile

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

/**
 * Identifier of the Kotlin Multiplatform Plugin.
 */
private const val KMP_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"

/**
 * Returns the [KotlinMultiplatformExtension] from `this` project.
 */
public val Project.kmpExtension: KotlinMultiplatformExtension
    get() = kotlinExtension as KotlinMultiplatformExtension

/**
 * Invokes [action] with the [KotlinMultiplatformExtension] passed as argument when the KMP plugin
 * is applied.
 */
public fun Project.withKmpPlugin(action: Project.(KotlinMultiplatformExtension) -> Unit) {
    pluginManager.withPlugin(KMP_PLUGIN_ID) {
        action(kmpExtension)
    }
}