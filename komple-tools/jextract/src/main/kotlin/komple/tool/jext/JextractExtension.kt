package komple.tool.jext

import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.JavaVersion
import org.gradle.api.provider.Property

/**
 * Jextract tool extension.
 */
public interface JextractExtension : KompleToolExtension, HasVersionSupport {

    /**
     * Version of the JDK.
     * Must be a version for which Jextract provides pre-built binaries.
     */
    public val jdkVersion: Property<JavaVersion>

    /**
     * Checksums for the downloaded tool per supported host.
     */
    public val checksums: Property<JextractChecksums>
}