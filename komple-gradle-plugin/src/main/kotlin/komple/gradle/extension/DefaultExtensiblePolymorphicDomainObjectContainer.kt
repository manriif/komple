package komple.gradle.extension

import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import java.util.function.IntFunction
import javax.inject.Inject

/**
 * Implementation of [ExtensiblePolymorphicDomainObjectContainer] with additional methods for
 * adding elements [T] without having to specify a name.
 *
 * Most of the time, only a single instance of a [T] will be registered with Komple.
 */
internal open class DefaultExtensiblePolymorphicDomainObjectContainer<T : Any> @Inject constructor(
    private val container: ExtensiblePolymorphicDomainObjectContainer<T>
) : ExtensiblePolymorphicDomainObjectContainer<T> by container {

    private val defaultNames = mutableMapOf<Class<*>, String>()

    ///////////////////////////////////////////////////////////////////////////
    // Name
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Registers the [name] that should be provided when an instance of [klass] is requested.
     */
    internal fun registerDefaultName(klass: Class<*>, name: String) {
        defaultNames[klass] = name
    }

    /**
     * Gets a default name for `this` [Class].
     */
    private fun Class<*>.defaultName(): String {
        return defaultNames[this]
            ?: simpleName.takeIf { it.isNotEmpty() }
            ?: error("Could not find a default name for `${this.simpleName}`")
    }

    ///////////////////////////////////////////////////////////////////////////
    // Create
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a domain object of [type] with a default name, and adds it to the container.
     *
     * @see [ExtensiblePolymorphicDomainObjectContainer.create].
     */
    fun <U : T> create(type: Class<U>): U {
        return create(type.defaultName(), type)
    }

    /**
     * Creates a domain object of [type] with a default name, adds it to the container, and
     * [configure]s it.
     *
     * @see [ExtensiblePolymorphicDomainObjectContainer.create].
     */
    fun <U : T> create(type: Class<U>, configure: U.() -> Unit): U {
        return create(type.defaultName(), type, configure)
    }

    /**
     * Creates a domain object of type [U] with a default name, and adds it to the container.
     *
     * @see [ExtensiblePolymorphicDomainObjectContainer.create].
     */
    inline fun <reified U : T> create(): U {
        return create(U::class.java)
    }

    /**
     * Creates a domain object of type [U] with a default name, adds it to the container, and
     * [configure]s it.
     *
     * @see [ExtensiblePolymorphicDomainObjectContainer.create].
     */
    inline fun <reified U : T> create(noinline configure: U.() -> Unit): U {
        return create(U::class.java, configure)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Register
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Defines a new domain object of [type] with a default name, which will be created when it is
     * required.
     *
     * @see [ExtensiblePolymorphicDomainObjectContainer.register].
     */
    fun <U : T> register(type: Class<U>): NamedDomainObjectProvider<U> {
        return register(type.defaultName(), type)
    }

    /**
     * Defines a new domain object of [type] with a default name, which will be created and
     * [configure]d when it is required.
     *
     * @see [ExtensiblePolymorphicDomainObjectContainer.register].
     */
    fun <U : T> register(type: Class<U>, configure: U.() -> Unit): NamedDomainObjectProvider<U> {
        return register(type.defaultName(), type, configure)
    }

    /**
     * Defines a new domain object of type [U] with a default name, which will be created when it is
     * required.
     *
     * @see [ExtensiblePolymorphicDomainObjectContainer.register].
     */
    inline fun <reified U : T> register(): NamedDomainObjectProvider<U> {
        return register(U::class.java)
    }

    /**
     * Defines a new domain object of type [U] with a default name, which will be created and
     * [configure]d when it is required.
     *
     * @see [ExtensiblePolymorphicDomainObjectContainer.register].
     */
    inline fun <reified U : T> register(
        noinline configure: U.() -> Unit
    ): NamedDomainObjectProvider<U> {
        return register(U::class.java, configure)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Collection
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Forced to overrides this because of java `default`.
     */
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "This method is deprecated in a parent class"
    )
    override fun <U> toArray(generator: IntFunction<Array<U>>): Array<U> {
        error("Not trivially implementable")
    }
}