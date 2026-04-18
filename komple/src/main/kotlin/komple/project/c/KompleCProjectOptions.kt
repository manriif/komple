@file:Suppress("UnstableApiUsage")

package komple.project.c

import komple.project.KompleCProject
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * Options for C project.
 */
public interface KompleCProjectOptions {

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
}

/**
 * Configures the convention values.
 */
internal fun KompleCProject.configureConventions(project: Project) {
    headerFile.convention(project.provider { error("Main header file was not set") })
    headerFilters.convention(headerFile)
    optimization.convention(Optimization.Level2)
}