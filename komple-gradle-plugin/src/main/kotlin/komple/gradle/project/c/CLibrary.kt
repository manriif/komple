package komple.gradle.project.c

import komple.project.c.CCompilation
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * C Library.
 */
public interface CLibrary {

    /**
     * Library related compilation.
     */
    public val compilation: CCompilation

    /**
     * Provider of the task responsible for compiling the library.
     * The generated library is the task output.
     */
    public val compileTaskProvider: TaskProvider<*>

    /**
     * Generated library file.
     * This can be passed to a task input to create an implicit dependency on [compileTaskProvider].
     */
    public val libraryFile: Provider<RegularFile>
}