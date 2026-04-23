package komple.project

import komple.platform.Platform
import komple.project.c.CCompileTask
import komple.project.c.CProject
import kotlin.reflect.KClass

/**
 * [KompleProject] configurator.
 */
public sealed interface ProjectConfigurator {

    /**
     * The [KompleProject] to configure.
     */
    public val project: KompleProject
}

///////////////////////////////////////////////////////////////////////////
// C
///////////////////////////////////////////////////////////////////////////

/**
 * Configurator for [komple.project.c.CProject].
 */
public interface CProjectConfigurator : ProjectConfigurator {

    override val project: CProject

    /**
     * Register a [CCompileTask] that is created when a compilation is created and configured with
     * [configure].
     */
    public fun <Task : CCompileTask<*, *>> registerCompileTask(
        klass: KClass<out Task>,
        configure: (Task.() -> Unit)? = null,
        platformFilter: (Platform) -> Boolean
    )
}

/**
 * Register a [CCompileTask] that is created when a compilation is created and configured with
 * [configure].
 */
public inline fun <reified Task : CCompileTask<*, *>> CProjectConfigurator.registerCompileTask(
    noinline configure: (Task.() -> Unit)? = null,
    noinline platformFilter: (Platform) -> Boolean
) {
    registerCompileTask(Task::class, configure, platformFilter)
}