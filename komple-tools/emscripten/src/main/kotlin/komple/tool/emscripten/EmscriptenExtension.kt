package komple.tool.emscripten

import komple.tool.extension.HasChecksumSupport
import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.provider.Property

/**
 * Emscripten tool extension.
 *
 * Note that [version] and [checksum] refers to the emsdk.
 */
public interface EmscriptenExtension :
    KompleToolExtension,
    HasVersionSupport,
    HasChecksumSupport {

    /**
     * Version of emscripten to pass to the emsdk `install` and `activate` commands.
     * A valid version is expected or `latest`.
     *
     * Default to the same version as the SDK.
     */
    public val emscriptenVersion: Property<String>
}