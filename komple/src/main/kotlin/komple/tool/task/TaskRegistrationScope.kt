package komple.tool.task

import komple.platform.HasHost
import komple.task.OutputToolTask
import komple.task.ToolTask
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Base scope for task registration.
 */
public interface TaskRegistrationScope<Extension : KompleToolExtension, Context : ToolTaskContext> :
    HasExtension<Extension>,
    HasHost {

    /**
     * Name of the tool.
     */
    public val toolName: String

    /**
     * Name of the tool for maximum compatibility with file systems for example.
     */
    public val toolNameCompat: String

    /**
     * Returns the project's [ProviderFactory].
     */
    public val providers: ProviderFactory

    /**
     * Registers a task of type [T], [configure]s it and returns it.
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean = false,
        configure: T.(context: Context) -> Unit
    ): TaskProvider<T>

    /**
     * Returns a task that skips the operation.
     */
    public fun skip(): TaskProvider<*>

    /**
     * Returns a task that always fails when executed.
     */
    public fun unsupported(): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T], [configure]s it and returns that registered task.
 */
public inline fun <reified T: Task, C : ToolTaskContext> TaskRegistrationScope<*, C>.register(
    cacheable: Boolean = false,
    noinline configure: T.(context: C) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    cacheable = cacheable,
    configure = configure
)

/**
 * Registers a task of type [T] subclass of [ToolTask] and [configure]s it.
 *
 * The [ToolTask.execEnvironment] is configured before [configure] is invoked.
 */
public inline fun <reified T : ToolTask, C : ToolTaskContext> TaskRegistrationScope<*, C>.tool(
    cacheable: Boolean = false,
    noinline configure: (T.(context: C) -> Unit)? = null
): TaskProvider<T> = register<T, C>(cacheable) { context ->
    this.execEnvironment = context.execEnvironment
    configure?.invoke(this, context)
}

/**
 * Registers a task of type [T] subclass of [ToolTask] and [configure]s it.
 *
 * The [OutputToolTask.execEnvironment], [OutputToolTask.tracker] and
 * [OutputToolTask.outputDirectory] properties are configured before [configure] is invoked.
 */
public inline fun <reified T : OutputToolTask, C : OutputToolTaskContext> TaskRegistrationScope<*, C>.outputTool(
    cacheable: Boolean = false,
    noinline configure: (T.(context: C) -> Unit)? = null
): TaskProvider<T> = tool(cacheable) { context ->
    this.tracker = context.tracker
    this.outputDirectory = context.outputDirectory
    configure?.invoke(this, context)
}