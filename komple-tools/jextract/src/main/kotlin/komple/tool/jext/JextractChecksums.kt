package komple.tool.jext

import org.gradle.api.provider.Property

/**
 * SHA-256 checksums for Jextract integrity check.
 */
public interface JextractChecksums {

    /**
     * Linux ARM64 host checksum.
     */
    public val linuxAarch64: Property<String>

    /**
     * Linux X64 host checksum.
     */
    public val linuxX64: Property<String>

    /**
     * MacOS ARM64 host checksum.
     */
    public val macosAarch64: Property<String>

    /**
     * MacOS X64 host checksum.
     */
    public val macosX64: Property<String>

    /**
     * Windows X64 host checksum.
     */
    public val windowsX64: Property<String>
}