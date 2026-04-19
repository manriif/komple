package komple.gradle.project.c

import komple.exec.KompleExecTask
import komple.project.KompleCProject
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested

/**
 * Task responsible for compiling and generating a C library.
 */
@CacheableTask
internal abstract class CCompileTask : KompleExecTask() {

    @get:Nested
    internal abstract val cProject: Property<KompleCProject>
}