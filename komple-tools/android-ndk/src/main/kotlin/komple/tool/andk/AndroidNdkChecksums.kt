package komple.tool.andk

import org.gradle.api.provider.Property

/**
 * SHA-1 checksums for Android NDK integrity check.
 */
public interface AndroidNdkChecksums {

    /**
     * Linux host checksum.
     */
    public val linux: Property<String>

    /**
     * MacOS host checksum.
     */
    public val macos: Property<String>

    /**
     * Windows host checksum.
     */
    public val windows: Property<String>
}