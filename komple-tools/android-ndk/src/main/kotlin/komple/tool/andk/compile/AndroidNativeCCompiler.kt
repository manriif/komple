package komple.tool.andk.compile

import komple.project.c.CCompileTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Task compiling C library targeting Android Native.
 */
@CacheableTask
internal abstract class AndroidNdkCCompileTask :
    CCompileTask<AndroidNativeCCompileWorkAction.Parameters, AndroidNativeCCompileWorkAction>() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val toolchainDirectory: DirectoryProperty

    @get:Nested
    abstract val params: Property<AndroidNativeCompilationParams>

    override val workActionClass: KClass<AndroidNativeCCompileWorkAction>
        get() = AndroidNativeCCompileWorkAction::class

    override fun AndroidNativeCCompileWorkAction.Parameters.configure() {
        this@AndroidNdkCCompileTask.let { task ->
            this.toolchainDirectory = task.toolchainDirectory
            this.params = task.params
        }
    }
}