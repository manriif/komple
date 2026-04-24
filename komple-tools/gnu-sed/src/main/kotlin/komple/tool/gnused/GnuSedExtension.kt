package komple.tool.gnused

import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.provider.Property

/**
 * GNU sed tool extension.
 */
public interface GnuSedExtension : KompleToolExtension, HasVersionSupport {

    /**
     * Checksum of the downloaded tool.
     */
    public val checksum: Property<String>
}