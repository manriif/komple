package komple.gradle.kmp

import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskProvider

/**
 * Configuration settings for Kotlin/Native [cinterop tool](https://kotlinlang.org/docs/native-c-interop.html).
 */
public interface CInteropSettings {

    /**
     * Include functions of the given names in the generated bindings.
     */
    public val excludedFunctions: ListProperty<String>

    /**
     * Include functions of the given names in the generated bindings.
     */
    public val noStringConversion: ListProperty<String>

    /**
     * Tasks contributing to the generation of sources files required for the cinterop tool.
     */
    public val sourcesTasks: ListProperty<TaskProvider<*>>

    /**
     * Adds additional options that are passed to the cinterop tool.
     */
    public fun extraOpts(vararg values: Any)

    /**
     * Adds additional options that are passed to the cinterop tool.
     */
    public fun extraOpts(values: List<Any>)
}

