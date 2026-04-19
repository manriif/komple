package komple.project

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * Komple project.
 */
public interface KompleProjectBase {

    /**
     * Package name for the output files.
     */
    @get:Input
    public val packageName: Property<String>

    /**
     * Sources files of the project.
     */
    @get:Input
    public val sourceFiles: ConfigurableFileCollection
}