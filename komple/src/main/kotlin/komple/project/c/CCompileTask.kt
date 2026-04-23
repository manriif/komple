package komple.project.c

import komple.exec.KompleExecTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
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

    @get:Nested
    public abstract val cProject: Property<CProject>

    @get:Nested
    public abstract val compilations: ListProperty<CCompilation>

    protected abstract val workActionClass: KClass<A>

    protected abstract fun P.configure()

    @TaskAction
    public fun compile() {
        val compilations = compilations.get()

        if (compilations.isEmpty()) {
            return
        }

        val workQueue = workerExecutor.noIsolation()

        let { task ->
            compilations.forEach { compilation ->
                workQueue.submit(workActionClass.java) {
                    this.cProject = task.cProject
                    this.compilation = compilation
                    configure()
                }
            }
        }
    }
}