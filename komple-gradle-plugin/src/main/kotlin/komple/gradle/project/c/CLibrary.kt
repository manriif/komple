package komple.gradle.project.c

import komple.platform.Platform
import komple.project.c.CCompilation
import komple.project.c.CLibraryType
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * C Library.
 */
public interface CLibrary : CCompilation {

    /**
     * Provider of the task responsible for compiling the library.
     * The generated library is the task output.
     */
    public val compileTaskProvider: TaskProvider<*>

    /**
     * Generated library file.
     * This can be passed to a task input to create an implicit dependency on [compileTaskProvider].
     */
    override val libraryFile: Provider<RegularFile>
}

/**
 * Implementation of [CCompilation].
 */
internal class CCompilationImpl(
    override val platform: Platform,
    override val libraryType: CLibraryType,
    override val libraryFile: Provider<RegularFile>
) : CCompilation

/**
 * Implementation of [CLibrary].
 */
internal class CLibraryImpl(
    override val compileTaskProvider: TaskProvider<*>,
    override val libraryFile: Provider<RegularFile>,
    override val libraryType: CLibraryType,
    override val platform: Platform
) : CLibrary