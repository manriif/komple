package komple.tool.compile

import komple.project.KompleProject
import komple.tool.extension.ExtensionScope
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Scope for [komple.compile.KompleCompilation] building.
 */
public interface CompilationBuilderScope<Extension : KompleToolExtension> :
    HasExtension<Extension> {

    /**
     * The [KompleProject] that lead to a new compilation.
     */
    public val project: KompleProject

    /**
     * Returns a directory where to store generated files.
     */
    public fun generatedDirectory(subdirectory: String): Provider<Directory>

    /**
     * Creates an extension of type [E], named after [name].
     * Arguments [args] are passed to [E] constructor.
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
     * The returned extension itself can be configured inside [configure].
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
 * The returned extension itself can be configured inside [configure].
 */
@IgnorableReturnValue
public inline fun <reified E : Any> CompilationBuilderScope<*>.createExtension(
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
 * The returned extension itself can be configured inside [configure].
 */
@IgnorableReturnValue
public inline fun <reified T : Task> CompilationBuilderScope<*>.registerTask(
    postfix: String,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = registerTask(
    postfix = postfix,
    type = T::class,
    configure = configure
)