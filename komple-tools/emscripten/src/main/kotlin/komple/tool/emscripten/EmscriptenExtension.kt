package komple.tool.emscripten

import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.provider.Property

/**
 * Emscripten tool extension.
 */
public interface EmscriptenExtension : KompleToolExtension, HasVersionSupport {

    /**
     * Checksum of the downloaded tool.
     */
    public val checksum: Property<String>
}