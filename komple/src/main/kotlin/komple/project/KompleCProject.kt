package komple.project

import komple.compile.c.Optimization
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
 * C project.
 */
public abstract class KompleCProject internal constructor(projectName: String) :
    KompleProject(projectName) {

    /**
     * Main header file.
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public abstract val headerFile: RegularFileProperty

    /**
     * Headers to include as they are discovered.
     * Default to [headerFile].
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public abstract val headerFilters: ConfigurableFileCollection

    /**
     * Directories to includes for header search.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public abstract val includeDirs: ConfigurableFileCollection

    /**
     * Optimization level.
     * Default to [Optimization.Level2].
     */
    @get:Input
    public abstract val optimization: Property<Optimization>

    /**
     * Pre-processor definitions.
     */
    @get:Input
    public abstract val defines: MapProperty<String, String>

    /**
     * Adds a definition with the value `1`.
     */
    public fun define(name: String) {
        defines.put(name, "1")
    }
}

///////////////////////////////////////////////////////////////////////////
// Conventions
///////////////////////////////////////////////////////////////////////////

/**
 * Configures the conventions values.
 */
@Suppress("UnstableApiUsage")
internal fun KompleCProject.configureConventions(project: Project) {
    headerFile.convention(project.provider { error("Main header file was not set") })
    headerFilters.convention(headerFile)
    optimization.convention(Optimization.Level2)
}