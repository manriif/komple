package komple.tool.jext.generator

import org.gradle.api.tasks.TaskProvider

/**
 * Configuration for binding generation.
 */
public interface JextractBindingGenerator : JextractCommandLineOptions {

    /**
     * Provider of the task responsible for binding generations.
     * The returned task output files are the generated bindings files.
     */
    public val generateTaskProvider: TaskProvider<JextractGenerateBindingsTask>
}