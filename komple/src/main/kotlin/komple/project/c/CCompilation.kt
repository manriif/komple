package komple.project.c

import komple.platform.Platform
import org.gradle.api.file.RegularFile
import java.io.Serializable

/**
 * Compilation for a C project.
 */
public interface CCompilation : Serializable {

    /**
     * Platform to compile to.
     */
    public val platform: Platform

    /**
     *  Type of library to produce.
     */
    public val libraryType: CLibraryType

    /**
     * File the library should be written to.
     */
    public val outputFile: RegularFile
}