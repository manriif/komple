package komple.gradle.project.c

import komple.exec.CommandExecutor
import komple.platform.Platform
import komple.project.c.CCompileTask
import org.gradle.api.provider.Provider
import kotlin.reflect.KClass

/**
 * Holder for [komple.project.c.CCompileTask] instantiation properties.
 */
internal class CCompileTaskFactory<Task : CCompileTask<*, *>>(
    val klass: KClass<out Task>,
    val configure: (Task.() -> Unit)?,
    val platformFilter: (Platform) -> Boolean,
    val commandExecutor: Provider<CommandExecutor>
)