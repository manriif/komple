package komple.project

import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection

/**
 * Project than can lead to compilation.
 */
public abstract class KompleProject internal constructor(private val projectName: String) : Named {

    /**
     * Sources files of the project.
     */
    public abstract val sourceFiles: ConfigurableFileCollection

    override fun getName(): String {
        return projectName
    }
}

///////////////////////////////////////////////////////////////////////////
// Conventions
///////////////////////////////////////////////////////////////////////////

/**
 * Configures the conventions values.
 */
@Suppress("UnstableApiUsage")
internal fun KompleProject.configureCommonConventions(project: Project) {
    sourceFiles.convention(project.provider { error("No source files were provided") })
}