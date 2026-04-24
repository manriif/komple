package komple.gradle.kmp

import komple.platform.Platform
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.konan.target.KonanTarget

/**
 * Identifier of the Kotlin Multiplatform Plugin.
 */
private const val KMP_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"

/**
 * Returns the [KotlinMultiplatformExtension] from `this` project.
 */
internal val Project.kmpExtension: KotlinMultiplatformExtension
    get() = kotlinExtension as KotlinMultiplatformExtension

/**
 * Invokes [action] with the [KotlinMultiplatformExtension] passed as argument when the KMP plugin
 * is applied.
 */
internal fun Project.withKmpPlugin(action: Project.(KotlinMultiplatformExtension) -> Unit) {
    pluginManager.withPlugin(KMP_PLUGIN_ID) {
        action(kmpExtension)
    }
}

/**
 * Returns the [Platform] representing `this` [KonanTarget].
 */
internal fun KonanTarget.toPlatform(): Platform = when (this) {
    ANDROID_ARM32 -> Platform.androidArm32
    ANDROID_ARM64 -> Platform.androidArm64
    ANDROID_X64 -> Platform.androidX64
    ANDROID_X86 -> Platform.androidX86
    IOS_ARM64 -> Platform.iosArm64
    IOS_SIMULATOR_ARM64 -> Platform.iosSimulatorArm64
    IOS_X64 -> Platform.iosX64
    LINUX_ARM64 -> Platform.linuxArm64
    LINUX_X64 -> Platform.linuxX64
    MACOS_ARM64 -> Platform.macosArm64
    MACOS_X64 -> Platform.macosX64
    MINGW_X64 -> Platform.mingwX64
    TVOS_ARM64 -> Platform.tvosArm64
    TVOS_SIMULATOR_ARM64 -> Platform.tvosSimulatorArm64
    TVOS_X64 -> Platform.tvosX64
    WATCHOS_ARM32 -> Platform.watchosArm32
    WATCHOS_ARM64 -> Platform.watchosArm64
    WATCHOS_DEVICE_ARM64 -> Platform.watchosDeviceArm64
    WATCHOS_SIMULATOR_ARM64 -> Platform.watchosSimulatorArm64
    WATCHOS_X64 -> Platform.watchosX64
    LINUX_ARM32_HFP -> error("Unsupported target: $this")
}