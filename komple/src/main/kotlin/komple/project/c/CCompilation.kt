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
     * File the library should be written to.
     */
    @get:OutputFile
    public val libraryFile: Provider<RegularFile>

    /**
     *  Type of library to produce.
     */
    @get:Input
    public val libraryType: Provider<CLibraryType>

    /**
     * Platform to compile to.
     */
    @get:Input
    public val platform: Provider<Platform>
}