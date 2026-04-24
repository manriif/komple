package komple.tool.wabt

import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.provider.Property

/**
 * WABT tool extension.
 */
public interface WabtExtension : KompleToolExtension, HasVersionSupport {

    /**
     * Checksum of the downloaded tool.
     */
    public val checksum: Property<String>
}