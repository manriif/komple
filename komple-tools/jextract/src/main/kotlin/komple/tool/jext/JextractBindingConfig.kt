package komple.tool.jext

import org.gradle.api.tasks.TaskProvider

/**
 * Configuration for binding generation.
 */
public interface JextractBindingConfig {

    /**
     * Provider of the task responsible for binding generations.
     * The returned task output files are the generated bindings files.
     */
    public val generatorTaskProvider: TaskProvider<JextractGenerateBindingsTask>
}