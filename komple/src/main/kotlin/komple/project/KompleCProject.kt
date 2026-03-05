package komple.project

import komple.compile.c.Optimization
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

/**
 * C project.
 */
public abstract class KompleCProject internal constructor(projectName: String) :
    KompleProject(projectName) {

    /**
     * Main header file.
     */
    public abstract val headerFile: RegularFileProperty

    /**
     * Headers to include as they are discovered.
     * Default to [headerFile].
     */
    public abstract val headerFilters: ConfigurableFileCollection

    /**
     * Directories to includes for header search.
     */
    public abstract val includeDirs: ConfigurableFileCollection

    /**
     * Optimization level.
     * Default to [Optimization.Level2].
     */
    public abstract val optimization: Property<Optimization>

    /**
     * Pre-processor definitions.
     */
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