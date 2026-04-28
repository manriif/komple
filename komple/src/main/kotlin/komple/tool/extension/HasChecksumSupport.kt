package komple.tool.extension

import org.gradle.api.provider.Property

/**
 * Tool checksum can be supplied.
 */
public interface HasChecksumSupport {

    /**
     * Checksum of the downloaded tool.
     */
    public val checksum: Property<String>
}