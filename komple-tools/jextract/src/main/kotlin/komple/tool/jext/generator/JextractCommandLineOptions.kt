package komple.tool.jext.generator

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input

/**
 * Options for Jextract command line.
 *
 * [Jextract](https://github.com/openjdk/jextract/blob/master/doc/GUIDE.md#command-line-option-reference)
 */
public interface JextractCommandLineOptions {

    /**
     * Name of the generated header class.
     * Default to a name derived from the main header file.
     */
    @get:Input
    public val headerClassName: Provider<String>

    /**
     * Include functions of the given names in the generated bindings.
     */
    @get:Input
    public val includeFunctions: ListProperty<String>

    /**
     * Include functions of the given names in the generated bindings.
     */
    @get:Input
    public val includeConstants: ListProperty<String>

    /**
     * Include structs of the given names in the generated bindings.
     */
    @get:Input
    public val includeStructs: ListProperty<String>

    /**
     * Include unions of the given names in the generated bindings.
     */
    @get:Input
    public val includeUnions: ListProperty<String>

    /**
     * Include typedefs of the given names in the generated bindings.
     */
    @get:Input
    public val includeTypedefs: ListProperty<String>

    /**
     * Include vars of the given names in the generated bindings.
     */
    @get:Input
    public val includeVars: ListProperty<String>
}