package komple.gradle.platform

import komple.platform.Architecture
import komple.platform.Host
import komple.platform.OperatingSystem

/**
 * Default implementation of [Host].
 */
private data class DefaultHost(
    override val operatingSystem: OperatingSystem.Host,
    override val architecture: Architecture.Host
) : Host

/**
 * Current host.
 */
internal val CurrentHost: Host = DefaultHost(
    operatingSystem = org.gradle.internal.os.OperatingSystem.current().run {
        when {
            isMacOsX -> OperatingSystem.MacOS
            isWindows -> OperatingSystem.Windows
            isLinux -> OperatingSystem.Linux
            else -> error("Unsupported operation system: $this")
        }
    },
    architecture = when (val architecture = System.getProperty("os.arch").lowercase()) {
        "aarch64", "arm64" -> Architecture.Arm64
        "x86_64", "amd64" -> Architecture.X64
        else -> error("Unsupported CPU architecture: $architecture")
    }
)