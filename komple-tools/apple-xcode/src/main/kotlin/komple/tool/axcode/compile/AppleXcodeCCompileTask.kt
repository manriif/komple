package komple.tool.axcode.compile

import komple.project.c.CCompileTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Task compiling C library targeting Apple Native.
 */
@CacheableTask
internal abstract class AppleXcodeCCompileTask :
    CCompileTask<AppleXcodeCCompileWorkAction.Parameters, AppleXcodeCCompileWorkAction>() {

    @get:Nested
    abstract val params: Property<AppleXcodeCompilationParams>

    override val workActionClass: KClass<AppleXcodeCCompileWorkAction>
        get() = AppleXcodeCCompileWorkAction::class

    override fun AppleXcodeCCompileWorkAction.Parameters.configure() {
        this@AppleXcodeCCompileTask.let { task ->
            this.params = task.params
        }
    }
}