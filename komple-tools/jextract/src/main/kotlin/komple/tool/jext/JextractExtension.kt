package komple.tool.jext

import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.JavaVersion
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/**
 * Jextract tool extension.
 */
public abstract class JextractExtension @Inject internal constructor(objects: ObjectFactory) :
    KompleToolExtension,
    HasVersionSupport {

    /**
     * Checksums for the downloaded tool per supported host.
     */
    public val checksums: JextractChecksums = objects.newInstance<JextractChecksums>()

    /**
     * Version of the JDK.
     * Must be a version for which Jextract provides pre-built binaries.
     */
    public abstract val jdkVersion: Property<JavaVersion>
}