package komple.project.c

import komple.platform.Platform
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile

/**
 * Compilation for a C project.
 */
public interface CCompilation {

    /**
     * Platform to compile to.
     */
    @get:Input
    public val platform: Platform

    /**
     *  Type of library to produce.
     */
    @get:Input
    public val libraryType: CLibraryType

    /**
     * File the library should be written to.
     */
    @get:OutputFile
    public val libraryFile: Provider<RegularFile>
}