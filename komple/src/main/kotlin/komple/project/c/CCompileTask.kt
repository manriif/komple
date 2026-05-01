package komple.project.c

import komple.exec.KompleExecTask
import komple.task.TaskContext
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.submit
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Task responsible for compiling and generating a C library.
 */
public abstract class CCompileTask<P : CCompileWorkAction.Parameters, A : CCompileWorkAction<P>> :
    KompleExecTask() {

    @get:Inject
    protected abstract val workerExecutor: WorkerExecutor

    @get:Internal
    public abstract val context: Property<TaskContext>

    @get:Nested
    public abstract val cProject: Property<CProject>

    @get:Nested
    public abstract val compilations: ListProperty<CCompilation>

    @get:Internal
    protected abstract val workActionClass: KClass<A>

    protected abstract fun P.configure()

    @TaskAction
    public fun compile() {
        if (!context.get().outputChanged.get()) {
            return
        }

        val compilations = compilations.get()

        if (compilations.isEmpty()) {
            return
        }

        val factory = execEnvironment.get().createCommandExecutorFactory()
        val workQueue = workerExecutor.noIsolation()
        val cProject = cProject.get()

        compilations.forEach { compilation ->
            workQueue.submit(workActionClass) {
                this.commandExecutorFactory = factory
                this.platform = compilation.platform
                this.libraryType = compilation.libraryType
                this.libraryFile = compilation.libraryFile.get().asFile
                this.sourceFiles = cProject.sourceFiles
                this.includeDirectories = cProject.includeDirectories
                this.compilerOptions = cProject.compilerOptions(compilation.platform)
                configure()
            }
        }
    }
}