package komple.gradle.project.c

import komple.platform.Platform
import komple.project.c.CLibraryType
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * C Library.
 */
public interface CLibrary {

    /**
     * Type of library.
     */
    public val type: CLibraryType

    /**
     * Targeted platform.
     */
    public val platform: Platform

    /**
     * Provider of the task responsible for compiling the library.
     */
    public val compileTaskProvider: TaskProvider<*>

    /**
     * Provider of the generated library file.
     */
    public val outputFile: Provider<RegularFile>
}