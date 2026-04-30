package komple.project

import komple.exec.HasExecEnvironment
import komple.exec.KompleExecTask
import komple.platform.HasHost
import komple.tool.extension.ExtensionScope
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Scope for [KompleProject] configuration.
 */
public interface ProjectConfigurationScope<Extension : KompleToolExtension> :
    HasExtension<Extension>,
    HasExecEnvironment,
    HasHost {

    /**
     * The [KompleProject] configurator.
     */
    public val configurator: ProjectConfigurator

    /**
     * Directory where the tool is installed.
     */
    public val installDirectory: Provider<Directory>

    /**
     * Returns a directory where to store generated files.
     */
    public fun generatedDirectory(): Provider<Directory>

    /**
     * Creates an extension of type [E], named after [name].
     * Arguments [args] are passed to [E] constructor.
     *
     * The returned extension itself can be configured inside [configure].
     */
    @IgnorableReturnValue
    public fun <E : Any> createExtension(
        name: String,
        type: KClass<E>,
        vararg args: Any,
        configure: (ExtensionScope<E>.() -> Unit)? = null
    ): E

    /**
     * Creates a task of type [T], named after the project name postfixed by [postfix].
     *
     * The returned task can be configured inside [configure].
     */
    @IgnorableReturnValue
    public fun <T : Task> registerTask(
        postfix: String,
        type: KClass<T>,
        configure: (T.() -> Unit)? = null
    ): TaskProvider<T>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Creates an extension of type [E], named after [name].
 * Arguments [args] are passed to [E] constructor.
 *
 * The returned extension itself can be configured inside [configure].
 */
@IgnorableReturnValue
public inline fun <reified E : Any> ProjectConfigurationScope<*>.createExtension(
    name: String,
    vararg args: Any,
    noinline configure: (ExtensionScope<E>.() -> Unit)? = null
): E = createExtension(
    name = name,
    type = E::class,
    args = args,
    configure = configure
)

/**
 * Creates a task of type [T], named after the project name postfixed by [postfix].
 *
 * The returned task can be configured inside [configure].
 */
@IgnorableReturnValue
public inline fun <reified T : Task> ProjectConfigurationScope<*>.registerTask(
    postfix: String,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = registerTask(
    postfix = postfix,
    type = T::class,
    configure = configure
)

/**
 * Creates a task of type [T], named after the project name postfixed by [postfix].
 *
 * The returned task can be configured inside [configure].
 */
@IgnorableReturnValue
public inline fun <reified T : KompleExecTask> ProjectConfigurationScope<*>.registerExecTask(
    postfix: String,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> {
    return registerTask<T>(postfix) {
        this.execEnvironment = this@registerExecTask.execEnvironment
        configure?.invoke(this)
    }
}