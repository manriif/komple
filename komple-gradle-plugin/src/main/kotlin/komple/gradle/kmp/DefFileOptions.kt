package komple.gradle.kmp

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * Options for the cinterop .def file.
 */
internal data class DefFileOptions(

    /**
     * Include functions of the given names in the generated bindings.
     */
    val excludedFunctions: Provider<List<String>>,

    /**
     * Include functions of the given names in the generated bindings.
     */
    val noStringConversion: Provider<List<String>>,

    /**
     * Def file.
     */
    val outputFile: Provider<RegularFile>,

    /**
     * Library file.
     */
    val libraryFile: Provider<RegularFile>,
)