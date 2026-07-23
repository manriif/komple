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
package komple.gradle.kmp

import komple.platform.Platform
import komple.project.c.CProject
import komple.project.c.allCompilerOptions
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
internal abstract class GenerateCInteropDefTaskInternal : GenerateCInteropDefTask() {

    @get:Nested
    abstract val cProject: Property<CProject>

    @get:Input
    abstract val platform: Property<Platform>

    @get:Input
    abstract val excludedFunctions: ListProperty<String>

    @get:Input
    abstract val noStringConversion: ListProperty<String>

    @get:OutputFile
    abstract val definitionFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val cProject = cProject.get()
        val platform = platform.get()
        val packageName = cProject.packageName.get()
        val libraryFile = libraryFile.orNull?.asFile
        val headerFileName = cProject.headerFile.get().asFile.name

        var content = """
            |language = C
            |package = $packageName
            |headers = $headerFileName
        """.trimMargin()

        libraryFile?.let { file ->
            content += "\nstaticLibraries = ${file.name}"
            content += "\nlibraryPaths = ${file.parentFile.absolutePath}"
        }

        cProject.headerFilters.takeUnless { it.isEmpty }?.let { files ->
            content += "\nheaderFilter = ${files.joinToString(" ") { it.name }}"
        }

        cProject.allCompilerOptions(platform).get().takeUnless { it.isEmpty() }?.let { options ->
            content += "\ncompilerOpts = ${options.joinToString(" ")}"
        }

        cProject.linkerOptions(platform).get().takeUnless { it.isEmpty() }?.let { options ->
            content += "\nlinkerOpts = ${options.joinToString(" ")}"
        }

        excludedFunctions.get().takeUnless { it.isEmpty() }?.let { functions ->
            content += "\nexcludedFunctions = ${functions.joinToString(" ")}"
        }

        noStringConversion.get().takeUnless { it.isEmpty() }?.let { functions ->
            content += "\nnoStringConversion = ${functions.joinToString(" ")}"
        }

        definitionFile.get().asFile
            .apply { parentFile.mkdirs() }
            .writeText(content)
    }
}