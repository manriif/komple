package komple.tool.andk

import komple.tool.andk.compile.AndroidNdkCompilationParams
import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/**
 * Android NDK tool extension.
 */
public abstract class AndroidNdkExtension @Inject internal constructor(objects: ObjectFactory) :
    KompleToolExtension,
    HasVersionSupport {

    /**
     * Checksums for the downloaded image per supported operating system.
     */
    public val checksums: AndroidNdkChecksums = objects.newInstance<AndroidNdkChecksums>()

    /**
     * Default parameters for C compilation.
     */
    public val compilationParams: AndroidNdkCompilationParams =
        objects.newInstance<AndroidNdkCompilationParams>()
}