package komple.gradle.project

import komple.gradle.extension.KompleRootProjectExtension
import komple.platform.Architecture
import komple.platform.OperatingSystem
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
 * Configures the [KotlinMultiplatformExtension].
 */
internal fun configureKmpExtension(
    root: KompleRootProjectExtension,
    kmp: KotlinMultiplatformExtension
) {
    root.extensibleProjects.all kProject@{
        kmp.targets.configureEach target@{
            configureKotlinTargetForKompleProject(this@kProject, this@target)
        }
    }
}

/**
 * Returns the [Platform] representing `this` [KonanTarget].
 */
internal fun KonanTarget.toPlatform(): Platform? = when (this) {
    KonanTarget.ANDROID_ARM32 -> Platform(OperatingSystem.Android, Architecture.Arm32)
    KonanTarget.ANDROID_ARM64 -> Platform(OperatingSystem.Android, Architecture.Arm64)
    KonanTarget.ANDROID_X64 -> Platform(OperatingSystem.Android, Architecture.X64)
    KonanTarget.ANDROID_X86 -> Platform(OperatingSystem.Android, Architecture.X86)
    KonanTarget.IOS_ARM64 -> Platform(OperatingSystem.IOS.Device, Architecture.Arm64)
    KonanTarget.IOS_SIMULATOR_ARM64 -> Platform(OperatingSystem.IOS.Simulator, Architecture.Arm64)
    KonanTarget.IOS_X64 -> Platform(OperatingSystem.IOS.Simulator, Architecture.X64)
    KonanTarget.LINUX_ARM64 -> Platform(OperatingSystem.Linux, Architecture.Arm64)
    KonanTarget.LINUX_X64 -> Platform(OperatingSystem.Linux, Architecture.X64)
    KonanTarget.MACOS_ARM64 -> Platform(OperatingSystem.MacOS, Architecture.Arm64)
    KonanTarget.MACOS_X64 -> Platform(OperatingSystem.MacOS, Architecture.X64)
    KonanTarget.MINGW_X64 -> Platform(OperatingSystem.Windows, Architecture.X64)
    KonanTarget.TVOS_ARM64 -> Platform(OperatingSystem.TvOS.Device, Architecture.Arm64)
    KonanTarget.TVOS_SIMULATOR_ARM64 -> Platform(OperatingSystem.TvOS.Simulator, Architecture.Arm64)
    KonanTarget.TVOS_X64 -> Platform(OperatingSystem.TvOS.Simulator, Architecture.X64)
    KonanTarget.WATCHOS_ARM32 -> Platform(OperatingSystem.WatchOS.Device, Architecture.Arm32)
    KonanTarget.WATCHOS_ARM64 -> Platform(OperatingSystem.WatchOS.Device, Architecture.Arm64)

    KonanTarget.WATCHOS_DEVICE_ARM64 ->
        Platform(OperatingSystem.WatchOS.DeviceGen2, Architecture.Arm64)

    KonanTarget.WATCHOS_SIMULATOR_ARM64 ->
        Platform(OperatingSystem.WatchOS.Simulator, Architecture.Arm64)

    KonanTarget.WATCHOS_X64 -> Platform(OperatingSystem.WatchOS.Simulator, Architecture.X64)
    KonanTarget.LINUX_ARM32_HFP -> error("Unsupported target: $this")
}