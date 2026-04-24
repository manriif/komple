package komple.tool.axcode

import komple.tool.axcode.compile.AppleXcodeCompilationParams
import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.provider.Property

/**
 * Apple Xcode tool extension.
 */
public interface AppleXcodeExtension : KompleToolExtension, HasVersionSupport {

    /**
     * Default parameters for C compilation.
     */
    public val compilationParams: Property<AppleXcodeCompilationParams>
}