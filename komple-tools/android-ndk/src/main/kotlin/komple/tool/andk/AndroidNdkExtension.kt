package komple.tool.andk

import komple.tool.andk.compile.AndroidNdkCompilationParams
import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.provider.Property

/**
 * Android NDK tool extension.
 */
public interface AndroidNdkExtension : KompleToolExtension, HasVersionSupport {

    /**
     * Checksums for the downloaded image per supported operating system.
     */
    public val checksums: Property<AndroidNdkChecksums>

    /**
     * Default parameters for C compilation.
     */
    public val compilationParams: Property<AndroidNdkCompilationParams>
}