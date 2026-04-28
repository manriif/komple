package komple.tool.jext.generator

import komple.project.c.CProject
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.tasks.TaskProvider

/**
 * Configuration for binding generation.
 */
public interface JextractBindingGenerator : Named {

    /**
     * The [CProject] this generator applies on.
     */
    public val cProject: CProject

    /**
     * Provider of the task responsible for binding generations.
     * The returned task output files are the generated bindings files.
     */
    public val generateTaskProvider: TaskProvider<JextractGenerateBindingsTask>

    /**
     * Command line options for the jextract tool.
     */
    public val options: JextractCommandLineOptions

    /**
     * Configures the options using [configuration].
     */
    public fun options(configuration: Action<JextractCommandLineOptions>)
}