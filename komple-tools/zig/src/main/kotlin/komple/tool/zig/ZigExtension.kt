package komple.tool.zig

import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import komple.tool.zig.compile.ZigCompilationParams
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/**
 * Zig tool extension.
 */
public abstract class ZigExtension @Inject internal constructor(objects: ObjectFactory) :
    KompleToolExtension,
    HasVersionSupport {

    /**
     * Zig's public key to check the downloaded tool.
     */
    public abstract val publicKey: Property<String>

    /**
     * Default parameters for C compilation.
     */
    public val compilationParams: ZigCompilationParams =
        objects.newInstance<ZigCompilationParams>()

    /**
     * Name of the tarball file.
     */
    internal abstract val archiveFileName: Property<String>
}