package komple.tool.compile

import komple.project.KompleProject
import komple.tool.extension.ExtensionScope
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
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
     * Creates an extension of type [CompilationExtension], named after [name].
     * Arguments [args] are passed to [CompilationExtension] constructor.
     * The returned extension itself can be configured inside [configure].
     */
    @IgnorableReturnValue
    public fun <CompilationExtension : Any> createExtension(
        name: String,
        type: KClass<CompilationExtension>,
        vararg args: Any,
        configure: (ExtensionScope<CompilationExtension>.() -> Unit)? = null
    ): CompilationExtension
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Creates an extension of type [CompilationExtension], named after [name].
 * Arguments [args] are passed to [CompilationExtension] constructor.
 * The returned extension itself can be configured inside [configure].
 */
@IgnorableReturnValue
public inline fun <reified CompilationExtension : Any> CompilationBuilderScope<*>.createExtension(
    name: String,
    vararg args: Any,
    noinline configure: (ExtensionScope<CompilationExtension>.() -> Unit)? = null
): CompilationExtension = createExtension(
    name = name,
    type = CompilationExtension::class,
    args = args,
    configure = configure
)