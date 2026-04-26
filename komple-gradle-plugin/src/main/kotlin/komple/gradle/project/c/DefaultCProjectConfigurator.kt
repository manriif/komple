package komple.gradle.project.c

import komple.exec.CommandExecutor
import komple.platform.Platform
import komple.project.CProjectConfigurator
import komple.project.c.CCompileTask
import komple.project.c.CProject
import org.gradle.api.provider.Provider
import kotlin.reflect.KClass

/**
 * Implementation of [CProjectConfigurator].
 */
internal class DefaultCProjectConfigurator(
    private val extension: CProjectExtension,
    private val commandExecutor: Provider<CommandExecutor>
) : CProjectConfigurator {

    override val project: CProject
        get() = extension.cProject

    override fun <Task : CCompileTask<*, *>> registerCompileTask(
        klass: KClass<out Task>,
        configure: (Task.() -> Unit)?,
        platformFilter: (Platform) -> Boolean
    ) {
        extension.compileTaskFactories.add(
            CCompileTaskFactory(
                klass = klass,
                configure = configure,
                platformFilter = platformFilter,
                commandExecutor = commandExecutor
            )
        )
    }
}