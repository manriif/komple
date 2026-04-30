package komple.project.c

import komple.exec.CommandExecutor
import komple.exec.execute
import komple.platform.Platform
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import java.io.File
import javax.inject.Inject
import kotlin.io.path.createTempFile

/**
 * Work action for C compilation.
 */
public abstract class CCompileWorkAction<Params : CCompileWorkAction.Parameters> :
    WorkAction<Params> {

    @get:Inject
    internal abstract val execOperations: ExecOperations

    protected val commandExecutor: CommandExecutor by lazy {
        parameters.commandExecutorFactory.get().create(execOperations)
    }

    protected fun compileStatic(
        compilerFlags: Array<out Any>,
        archiverFlags: Array<out Any>,
    ) {
        val libraryFile = parameters.libraryFile.get()

        val sourceFilesWithObjects = parameters.sourceFiles.get().associateWith { file ->
            createTempFile(
                prefix = file.nameWithoutExtension,
                suffix = libraryFile.extension
            ).toFile()
        }

        val compilerOptions = parameters.compilerOptions.get().toTypedArray()

        sourceFilesWithObjects.forEach { (sourceFile, objectFile) ->
            commandExecutor.execute(
                *compilerFlags,
                "-c",
                sourceFile.absolutePath,
                "-o",
                objectFile.absolutePath,
                *compilerOptions,
            )
        }

        val objectFiles = sourceFilesWithObjects.values
            .map { it.absolutePath }
            .toTypedArray()

        commandExecutor.execute(
            *archiverFlags,
            "rcs",
            libraryFile.absolutePath,
            *objectFiles
        )
    }

    protected fun compileShared(compilerFlags: Array<out Any>) {
        val libraryFile = parameters.libraryFile.get()

        val sourceFiles = parameters.sourceFiles.get()
            .map { it.absolutePath }
            .toTypedArray()

        val compilerOptions = parameters.compilerOptions.get().toTypedArray()

        commandExecutor.execute(
            *compilerFlags,
            "-o",
            libraryFile.absolutePath,
            *sourceFiles,
            *compilerOptions,
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    public interface Parameters : WorkParameters {

        public val commandExecutorFactory: Property<CommandExecutor.Factory>
        public val platform: Property<Platform>
        public val libraryFile: Property<File>
        public val libraryType: Property<CLibraryType>
        public val sourceFiles: ListProperty<File>
        public val compilerOptions: ListProperty<String>
    }
}