package komple.platform

import java.io.Serializable

/**
 * Platform information.
 */
public data class Platform(
    val operatingSystem: OperatingSystem,
    val architecture: Architecture,
) : Serializable {

    /**
     * Platform primary name.
     */
    val name: String
        get() = "${operatingSystem.name}_${architecture.name}"

    /**
     * Platform alternative name.
     */
    public val altName: String
        get() = "${operatingSystem.altName}_${architecture.altName}"

    /**
     * Set of common platforms.
     */
    public companion object {

        public val androidArm32: Platform =
            Platform(OperatingSystem.Android, Architecture.Arm32)

        public val androidArm64: Platform =
            Platform(OperatingSystem.Android, Architecture.Arm64)

        public val androidX64: Platform =
            Platform(OperatingSystem.Android, Architecture.X64)

        public val androidX86: Platform =
            Platform(OperatingSystem.Android, Architecture.X86)

        public val iosArm64: Platform =
            Platform(OperatingSystem.IOS.Device, Architecture.Arm64)

        public val iosSimulatorArm64: Platform =
            Platform(OperatingSystem.IOS.Simulator, Architecture.Arm64)

        public val iosX64: Platform =
            Platform(OperatingSystem.IOS.Simulator, Architecture.X64)

        public val linuxArm64: Platform =
            Platform(OperatingSystem.Linux, Architecture.Arm64)

        public val linuxX64: Platform =
            Platform(OperatingSystem.Linux, Architecture.X64)

        public val macosArm64: Platform =
            Platform(OperatingSystem.MacOS, Architecture.Arm64)

        public val macosX64: Platform =
            Platform(OperatingSystem.MacOS, Architecture.X64)

        public val mingwX64: Platform =
            Platform(OperatingSystem.Windows, Architecture.X64)

        public val tvosArm64: Platform =
            Platform(OperatingSystem.TvOS.Device, Architecture.Arm64)

        public val tvosSimulatorArm64: Platform =
            Platform(OperatingSystem.TvOS.Simulator, Architecture.Arm64)

        public val tvosX64: Platform =
            Platform(OperatingSystem.TvOS.Simulator, Architecture.X64)

        public val watchosArm32: Platform =
            Platform(OperatingSystem.WatchOS.Device, Architecture.Arm32)

        public val watchosArm64: Platform =
            Platform(OperatingSystem.WatchOS.Device, Architecture.Arm64)

        public val watchosDeviceArm64: Platform =
            Platform(OperatingSystem.WatchOS.DeviceGen2, Architecture.Arm64)

        public val watchosSimulatorArm64: Platform =
            Platform(OperatingSystem.WatchOS.Simulator, Architecture.Arm64)

        public val watchosX64: Platform =
            Platform(OperatingSystem.WatchOS.Simulator, Architecture.X64)
    }
}