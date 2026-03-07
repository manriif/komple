package komple.tool.jext

import komple.KOMPLE_EXEC_SERVICE_NAME
import komple.exec.CommandBuilder
import komple.exec.ExecService
import komple.project.KompleCProject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task responsible for bindings generation.
 * Outputs the generated bindings files.
 *
 * [Jextract](https://github.com/openjdk/jextract/blob/master/doc/GUIDE.md#command-line-option-reference)
 */
@CacheableTask
public abstract class JextractGenerateBindingsTask internal constructor() : DefaultTask() {

    @get:ServiceReference(KOMPLE_EXEC_SERVICE_NAME)
    internal abstract val execService: Property<ExecService>

    /**
     * Name of the generated header class.
     * Default to a name derived from the main header file.
     */
    public abstract val headerClassName: Provider<String>

    /**
     * Include functions of the given names in the generated bindings.
     */
    public abstract val includeFunctions: ListProperty<String>

    /**
     * Include functions of the given names in the generated bindings.
     */
    public abstract val includeConstants: ListProperty<String>

    /**
     * Include structs of the given names in the generated bindings.
     */
    public abstract val includeStructs: ListProperty<String>

    /**
     * Include unions of the given names in the generated bindings.
     */
    public abstract val includeUnions: ListProperty<String>

    /**
     * Include typedefs of the given names in the generated bindings.
     */
    public abstract val includeTypedefs: ListProperty<String>

    /**
     * Include vars of the given names in the generated bindings.
     */
    public abstract val includeVars: ListProperty<String>

    /**
     * The project
     */
    internal abstract val project: Property<KompleCProject>

    /**
     * Directory where generated files are written.
     */
    @get:OutputDirectory
    internal abstract val outputDirectory: DirectoryProperty

    @TaskAction
    public fun generate() {
        val command = CommandBuilder(
            "jextract",

            ).build()

        execService.get().exec()
    }
}