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
package komple.tool.jext.generator

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
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
    public val headerClassName: Property<String>

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