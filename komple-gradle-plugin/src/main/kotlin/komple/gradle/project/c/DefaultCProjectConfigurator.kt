package komple.gradle.project.c

import komple.exec.ExecEnvironment
import komple.platform.Platform
import komple.project.CProjectConfigurator
import komple.project.c.CCompileTask
import komple.project.c.CProject
import kotlin.reflect.KClass

/**
 * Implementation of [CProjectConfigurator].
 */
internal class DefaultCProjectConfigurator(
    private val extension: CProjectExtension,
    private val execEnvironment: ExecEnvironment
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
                execEnvironment = execEnvironment
            )
        )
    }
}