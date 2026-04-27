package komple.tool.cmake

import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/**
 * CMake tool extension.
 */
public abstract class CmakeExtension @Inject internal constructor(objects: ObjectFactory) :
    KompleToolExtension,
    HasVersionSupport {

    /**
     * Checksum of the downloaded tool per supported host.
     */
    public val checksums: CmakeChecksums = objects.newInstance<CmakeChecksums>()
}