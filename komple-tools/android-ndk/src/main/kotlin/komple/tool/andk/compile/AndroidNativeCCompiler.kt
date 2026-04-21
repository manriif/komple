package komple.tool.andk.compile

import komple.project.c.CCompileTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Task compiling C library targeting Android Native.
 */
@CacheableTask
internal abstract class AndroidNdkCCompileTask :
    CCompileTask<AndroidNativeCCompileWorkAction.Parameters, AndroidNativeCCompileWorkAction>() {

    @get:Input
    abstract val params: Property<AndroidNativeCompilationParams>

    override val workActionClass: KClass<AndroidNativeCCompileWorkAction>
        get() = AndroidNativeCCompileWorkAction::class

    override fun AndroidNativeCCompileWorkAction.Parameters.configure() {
        this.params = this@AndroidNdkCCompileTask.params
    }
}