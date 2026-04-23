package komple.project.c

import komple.exec.ExecService
import komple.exec.execute
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import javax.inject.Inject
import kotlin.io.path.createTempFile

/**
 * Work action for C compilation.
 */
public abstract class CCompileWorkAction<Params : CCompileWorkAction.Parameters> :
    WorkAction<Params> {

    @get:Inject
    protected abstract val execService: ExecService

    protected fun compileStatic(
        compilerFlags: Array<out Any>,
        archiverFlags: Array<out Any>,
    ) {
        val project = parameters.cProject.get()
        val compilation = parameters.compilation.get()
        val libraryFile = compilation.libraryFile.get().asFile

        val sourceFilesWithObjects = project.sourceFiles.files.associateWith { file ->
            createTempFile(
                prefix = "${project.libraryName}${file.nameWithoutExtension}",
                suffix = libraryFile.extension
            ).toFile()
        }

        val compilerOptions = project
            .compilerOptions(compilation.platform)
            .get()
            .toTypedArray()

        sourceFilesWithObjects.forEach { (sourceFile, objectFile) ->
            execService.execute(
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

        execService.execute(
            *archiverFlags,
            "rcs",
            libraryFile.absolutePath,
            *objectFiles
        )
    }

    protected fun compileShared(compilerFlags: Array<out Any>) {
        val project = parameters.cProject.get()
        val compilation = parameters.compilation.get()
        val libraryFile = compilation.libraryFile.get().asFile

        val sourceFiles = project.sourceFiles
            .map { it.absolutePath }
            .toTypedArray()

        val compilerOptions = project
            .compilerOptions(compilation.platform)
            .get()
            .toTypedArray()

        execService.execute(
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

        public val cProject: Property<CProject>
        public val compilation: Property<CCompilation>
    }
}