package komple.project.c

import komple.exec.KompleExecTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
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
    internal abstract val cProject: Property<CProject>

    @get:Input
    internal abstract val compilations: ListProperty<CCompilation>

    protected abstract val workActionClass: KClass<A>

    @TaskAction
    public fun compile() {
        val compilations = compilations.get()

        if (compilations.isEmpty()) {
            return
        }

        val cProject = cProject.get()
        val workQueue = workerExecutor.noIsolation()

        let { task ->
            compilations.forEach { compilation ->
                workQueue.submit(workActionClass.java) {
                    this.
                    configure()
                }
            }
        }
    }

    protected abstract fun P.configure(
    )
}