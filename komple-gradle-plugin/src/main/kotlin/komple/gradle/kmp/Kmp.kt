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
    KonanTarget.ANDROID_ARM32 -> Platform.androidArm32
    KonanTarget.ANDROID_ARM64 -> Platform.androidArm64
    KonanTarget.ANDROID_X64 -> Platform.androidX64
    KonanTarget.ANDROID_X86 -> Platform.androidX86
    KonanTarget.IOS_ARM64 -> Platform.iosArm64
    KonanTarget.IOS_SIMULATOR_ARM64 -> Platform.iosSimulatorArm64
    KonanTarget.IOS_X64 -> Platform.iosX64
    KonanTarget.LINUX_ARM64 -> Platform.linuxArm64
    KonanTarget.LINUX_X64 -> Platform.linuxX64
    KonanTarget.MACOS_ARM64 -> Platform.macosArm64
    KonanTarget.MACOS_X64 -> Platform.macosX64
    KonanTarget.MINGW_X64 -> Platform.mingwX64
    KonanTarget.TVOS_ARM64 -> Platform.tvosArm64
    KonanTarget.TVOS_SIMULATOR_ARM64 -> Platform.tvosSimulatorArm64
    KonanTarget.TVOS_X64 -> Platform.tvosX64
    KonanTarget.WATCHOS_ARM32 -> Platform.watchosArm32
    KonanTarget.WATCHOS_ARM64 -> Platform.watchosArm64
    KonanTarget.WATCHOS_DEVICE_ARM64 -> Platform.watchosDeviceArm64
    KonanTarget.WATCHOS_SIMULATOR_ARM64 -> Platform.watchosSimulatorArm64
    KonanTarget.WATCHOS_X64 -> Platform.watchosX64
    KonanTarget.LINUX_ARM32_HFP -> error("Unsupported target: $this")
}