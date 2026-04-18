package komple.tool.jext.generator

import komple.KOMPLE_EXEC_SERVICE_NAME
import komple.exec.CommandBuilder
import komple.exec.ExecService
import komple.project.KompleCProject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Task responsible for bindings generation.
 * Outputs the generated bindings files.
 */
@CacheableTask
public abstract class JextractGenerateBindingsTask internal constructor() : DefaultTask() {

    @get:ServiceReference(KOMPLE_EXEC_SERVICE_NAME)
    internal abstract val execService: Property<ExecService>

    @get:Nested
    internal abstract val cProject: Property<KompleCProject>

    @get:Nested
    internal abstract val cliOptions: Property<JextractCommandLineOptions>

    @get:OutputDirectory
    internal abstract val outputDirectory: DirectoryProperty

    @TaskAction
    public fun generate() {
        val project = cProject.get()
        val options = cliOptions.get()

        val command = CommandBuilder("jextract")
            .append("--output", outputDirectory.get().asFile.absolutePath)
            .append("--target-package", project.packageName.get())
            .append("--header-class-name", options.headerClassName.get())
            .appendValues("--include-dir", project.includeDirs, File::getAbsolutePath)
            .appendValues("--include-function", options.includeFunctions.get())
            .appendValues("--include-constant", options.includeConstants.get())
            .appendValues("--include-struct", options.includeStructs.get())
            .appendValues("--include-union", options.includeUnions.get())
            .appendValues("--include-typedef", options.includeTypedefs.get())
            .appendValues("--include-var", options.includeVars.get())
            .appendValues("--define-macro", project.defines.get().entries) { (key, value) ->
                "$key=$value"
            }
            .append(project.headerFile.get().asFile.absolutePath)
            .build()

        execService.get().execute(command)
    }

    /**
     * Appends each [optionValues] prefixed by [optionName].
     */
    private inline fun <T : Any> CommandBuilder.appendValues(
        optionName: String,
        optionValues: Iterable<T>,
        toString: (T) -> String = Any::toString
    ): CommandBuilder = apply {
        optionValues.forEach { value ->
            append(optionName)
            append(toString(value))
        }
    }
}