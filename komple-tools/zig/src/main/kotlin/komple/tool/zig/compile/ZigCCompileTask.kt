package komple.tool.zig.compile

import komple.project.c.CCompileTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Task compiling C library using Zig compilator.
 */
@CacheableTask
internal abstract class ZigCCompileTask :
    CCompileTask<ZigCCompileWorkAction.Parameters, ZigCCompileWorkAction>() {

    @get:Nested
    abstract val params: Property<ZigCompilationParams>

    override val workActionClass: KClass<ZigCCompileWorkAction>
        get() = ZigCCompileWorkAction::class

    override fun ZigCCompileWorkAction.Parameters.configure() {
        this@ZigCCompileTask.let { task ->
            this.params = task.params
        }
    }
}