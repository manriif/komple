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
package komple.project.c

import komple.exec.KompleExecTask
import komple.task.TaskStateTracker
import komple.task.hasChanged
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
    public abstract val tracker: Property<TaskStateTracker>

    @get:Nested
    public abstract val cProject: Property<CProject>

    @get:Nested
    public abstract val compilations: ListProperty<CCompilation>

    @get:Internal
    protected abstract val workActionClass: KClass<A>

    protected abstract fun P.configure()

    @TaskAction
    internal fun compile() {
        val tracker = tracker.get()

        if (!tracker.hasChanged()) {
            didWork = false
            return logger.lifecycle("Reusing previously compiled libraries")
        }

        val compilations = compilations.get()

        if (compilations.isEmpty()) {
            return logger.lifecycle("No Library to compile")
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
                this.compilerOptions = cProject.allCompilerOptions(compilation.platform.get())
                configure()
            }
        }
    }
}