package komple.extension

import kotlin.reflect.KProperty1

/**
 * Extends [Extension] DSL.
 */
public interface ExtensionScope<Extension : Any> : ExtensionRegistrationScope {

    /**
     * Context extension.
     */
    public val extension: Extension

    /**
     * Adds the child extension [Child] already declared in [Extension] and optionally [configure]s
     * it.
     */
    @IgnorableReturnValue
    public fun <Child : Any> add(
        property: KProperty1<Extension, Child>,
        configure: (ExtensionScope<Child>.() -> Unit)? = null
    ): Child
}