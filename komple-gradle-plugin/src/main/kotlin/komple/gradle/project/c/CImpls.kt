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
package komple.gradle.project.c

import komple.platform.Platform
import komple.project.c.CCompilation
import komple.project.c.CLibraryType
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Implementation of [CCompilation].
 */
internal abstract class CCompilationImpl : CCompilation {

    abstract override val libraryFile: RegularFileProperty
    abstract override val libraryType: Property<CLibraryType>
    abstract override val platform: Property<Platform>
}

/**
 * Implementation of [CLibrary].
 */
internal class CLibraryImpl(
    override val compileTaskProvider: TaskProvider<*>,
    override val libraryFile: Provider<RegularFile>,
    override val compilation: CCompilation
) : CLibrary