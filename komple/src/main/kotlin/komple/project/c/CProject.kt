/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package komple.project.c

import komple.platform.Platform
import komple.project.KompleProject
import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * Base for C project.
 */
public interface CProject : KompleProject {

    /**
     * Name of the library to generate.
     * Default to the project name.
     */
    @get:Input
    public val libraryName: Property<String>

    /**
     * Main header file.
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val headerFile: RegularFileProperty

    /**
     * Headers to include as they are discovered.
     * Default to [headerFile].
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val headerFilters: ConfigurableFileCollection

    /**
     * Directories to includes for header search.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val includeDirectories: ConfigurableFileCollection

    /**
     * Pre-processor definitions for all the platforms.
     */
    @get:Input
    public val definitions: MapProperty<String, String>

    /**
     * Compiler options for all the platforms.
     */
    @get:Input
    public val compilerOptions: ListProperty<String>

    /**
     * Linker options for all the platforms.
     */
    @get:Input
    public val linkerOptions: ListProperty<String>

    /**
     * Default optimization level.
     */
    @get:Input
    @get:Optional
    public val optimization: Property<COptimization>

    /**
     * Sets the pre-processor definitions for the specified [platform].
     */
    public fun definition(
        platform: Platform,
        configure: Action<MutableMap<String, String>>
    )

    /**
     * Returns the string definitions has a list of the form `key=value` for the specified
     * [platform].
     */
    public fun definitions(platform: Platform): Provider<List<String>>

    /**
     * Sets the compiler options for the specified [platform].
     */
    public fun compilerOptions(
        platform: Platform,
        configure: Action<MutableList<String>>
    )

    /**
     * Returns the compiler options, without [definition] and without [optimization], for the
     * specified [platform].
     */
    public fun compilerOptions(platform: Platform): Provider<List<String>>

    /**
     * Sets the linker options for the specified [platform].
     */
    public fun linkerOptions(
        platform: Platform,
        configure: Action<MutableList<String>>
    )

    /**
     * Returns the linker options, for the specified [platform].
     */
    public fun linkerOptions(platform: Platform): Provider<List<String>>

    /**
     * Sets the [optimization] level for the specified [platform].
     */
    public fun optimization(
        platform: Platform,
        optimization: COptimization
    )

    /**
     * Returns the optimization for the [platform].
     */
    public fun optimization(platform: Platform): Provider<String>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Returns all the compiler options, including [CProject.definitions] and [CProject.optimization],
 * for the specified [platform].
 */
public fun CProject.allCompilerOptions(platform: Platform): Provider<List<String>> =
    compilerOptions(platform)
        .zip(optimization(platform), List<String>::plus)
        .zip(definitions(platform), List<String>::plus)