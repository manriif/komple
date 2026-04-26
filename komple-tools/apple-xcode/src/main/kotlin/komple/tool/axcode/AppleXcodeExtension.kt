package komple.tool.axcode

import komple.tool.axcode.compile.AppleXcodeCompilationParams
import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/**
 * Apple Xcode tool extension.
 */
public abstract class AppleXcodeExtension @Inject internal constructor(objects: ObjectFactory) :
    KompleToolExtension,
    HasVersionSupport {

    /**
     * Default parameters for C compilation.
     */
    public val compilationParams: AppleXcodeCompilationParams =
        objects.newInstance<AppleXcodeCompilationParams>()
}