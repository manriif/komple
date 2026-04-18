@file:Suppress("UnstableApiUsage")

package komple.project

import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

/**
 * Project than can lead to compilation.
 */
public sealed class KompleProject(@Internal private val projectName: String) : Named {

    /**
     * Package name for the output files.
     */
    @get:Input
    public abstract val packageName: Property<String>

    /**
     * Sources files of the project.
     */
    @get:Input
    public abstract val sourceFiles: ConfigurableFileCollection

    override fun getName(): String {
        return projectName
    }
}

/**
 * Configures the conventions values.
 */
@Suppress("UnstableApiUsage")
internal fun KompleProject.configureCommonConventions(project: Project) {
    packageName.convention(project.provider { "No package name was provided" })
    sourceFiles.convention(project.provider { error("No source files were provided") })
}