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

import komple.exec.Command
import komple.exec.KompleExecTask
import komple.project.c.CProject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Task responsible for bindings generation.
 * Outputs the generated bindings files.
 */
@CacheableTask
public abstract class JextractGenerateBindingsTask internal constructor() : KompleExecTask() {

    @get:Input
    internal abstract val executable: Property<String>

    @get:Nested
    internal abstract val cProject: Property<CProject>

    @get:Nested
    internal abstract val cliOptions: Property<JextractCommandLineOptions>

    @get:Internal
    public abstract val buildDirectory: DirectoryProperty

    @get:OutputDirectory
    internal abstract val outputDirectory: DirectoryProperty

    @TaskAction
    public fun generate() {
        val project = cProject.get()
        val options = cliOptions.get()
        val buildDirectory = buildDirectory.get().asFile
        val outputDirectory = outputDirectory.get().asFile

        outputDirectory.deleteRecursively()

        val args = buildString {
            appendArg("--output", outputDirectory.absolutePath)
            appendArg("--target-package", project.packageName.get())
            appendArg("--header-class-name", options.headerClassName.get())
            appendValues("--include-dir", project.includeDirectories, File::getAbsolutePath)
            appendValues("--include-function", options.includeFunctions.get())
            appendValues("--include-constant", options.includeConstants.get())
            appendValues("--include-struct", options.includeStructs.get())
            appendValues("--include-union", options.includeUnions.get())
            appendValues("--include-typedef", options.includeTypedefs.get())
            appendValues("--include-var", options.includeVars.get())

            appendValues("--define-macro", project.definitions.get().entries) { (key, value) ->
                "$key=$value"
            }

            append(project.headerFile.get().asFile.absolutePath)
        }

        val argsFile = buildDirectory.resolve("args.txt")

        buildDirectory.mkdirs()
        argsFile.writeText(args)

        newCommandExecutor().execute(
            command = Command(executable.get(), "\"@args.txt\""),
            workingDirectory = buildDirectory,
        )
    }

    /**
     * Appends each [args] postfixed by a new line.
     */
    private fun StringBuilder.appendArg(vararg args: String) {
        args.forEach { arg ->
            if (arg.contains(' ')) {
                appendLine("\"$arg\"")
            } else {
                appendLine(arg)
            }
        }
    }

    /**
     * Appends each [optionValues] prefixed by [optionName].
     */
    private inline fun <T : Any> StringBuilder.appendValues(
        optionName: String,
        optionValues: Iterable<T>,
        toString: (T) -> String = Any::toString
    ) {
        optionValues.forEach { value ->
            appendArg(optionName)
            appendArg(toString(value))
        }
    }
}