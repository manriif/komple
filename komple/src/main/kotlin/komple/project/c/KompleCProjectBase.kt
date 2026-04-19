@file:Suppress("UnstableApiUsage")

package komple.project.c

import komple.platform.Platform
import komple.project.KompleProjectBase
import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * Base fpr Komple C project.
 */
public interface KompleCProjectBase : KompleProjectBase {

    /**
     * Name of the library to generate.
     * Default to the project name.
     */
    @get:Input
    public val libraryName: Property<String>

    /**
     * Main header file.
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val headerFile: RegularFileProperty

    /**
     * Headers to include as they are discovered.
     * Default to [headerFile].
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val headerFilters: ConfigurableFileCollection

    /**
     * Directories to includes for header search.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val includeDirs: ConfigurableFileCollection

    /**
     * Optimization level.
     * Default to [Optimization.Level2].
     */
    @get:Input
    public val optimization: Property<Optimization>

    /**
     * Compiler options.
     */
    public val compilerOptions: ListProperty<String>

    /**
     * Linker options.
     */
    @get:Input
    public val linkerOptions: ListProperty<String>

    /**
     * Pre-processor definitions.
     */
    @get:Input
    public val defines: MapProperty<String, String>

    /**
     * Adds a definition with the value `1`.
     */
    public fun define(name: String) {
        defines.put(name, "1")
    }

    /**
     * Sets the compiler options for the specified [platform].
     */
    public fun compilerOptions(
        platform: Platform,
        configure: Action<in ListProperty<String>>
    )

    /**
     * Sets the linker options for the specified [platform].
     */
    public fun linkerOptions(
        platform: Platform,
        configure: Action<in ListProperty<String>>
    )
}