package komple.tool.cmake

import org.gradle.api.provider.Property

/**
 * SHA-256 checksums for CMake integrity check.
 */
public interface CmakeChecksums {

    /**
     * Linux ARM64 host checksum.
     */
    public val linuxAarch64: Property<String>

    /**
     * Linux X64 host checksum.
     */
    public val linuxX64: Property<String>

    /**
     * MacOS Universal host checksum.
     */
    public val macos: Property<String>

    /**
     * Windows ARM64 host checksum.
     */
    public val windowsAarch64: Property<String>

    /**
     * Windows X64 host checksum.
     */
    public val windowsX64: Property<String>
}