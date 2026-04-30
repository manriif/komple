package komple.project

import org.gradle.api.Named
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * Komple project.
 */
public interface KompleProject : Named {

    /**
     * Package name for the output files.
     */
    @get:Input
    public val packageName: Property<String>

    /**
     * Sources files of the project.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val sourceFiles: ConfigurableFileCollection
}