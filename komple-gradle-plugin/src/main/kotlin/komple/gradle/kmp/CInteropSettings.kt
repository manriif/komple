package komple.gradle.kmp

import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskProvider

/**
 * Configuration settings for Kotlin/Native [cinterop tool](https://kotlinlang.org/docs/native-c-interop.html).
 */
public interface CInteropSettings {

    /**
     * Provider of the task responsible to generate the interop def file.
     */
    public val generateDefFileTaskProvider: TaskProvider<*>

    /**
     * Include functions of the given names in the generated bindings.
     */
    public val excludedFunctions: ListProperty<String>

    /**
     * Include functions of the given names in the generated bindings.
     */
    public val noStringConversion: ListProperty<String>

    /**
     * Adds additional options that are passed to the cinterop tool.
     */
    public fun extraOpts(vararg values: Any)

    /**
     * Adds additional options that are passed to the cinterop tool.
     */
    public fun extraOpts(values: List<Any>)
}

