package komple.gradle.kmp

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * Options for the cinterop .def file.
 */
internal data class DefFileOptions(

    /**
     * Function names that should be ignored.
     */
    val excludedFunctions: Provider<List<String>>,

    /**
     * Functions whose const char* parameters should not be auto-converted to Kotlin Strings.
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