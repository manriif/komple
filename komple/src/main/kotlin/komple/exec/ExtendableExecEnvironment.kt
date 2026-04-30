package komple.exec

import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Nested

/**
 * [ExecEnvironment] which can be extended with other environments.
 */
public interface ExtendableExecEnvironment : ExecEnvironment {

    /**
     * Extras [ExecEnvironment].
     */
    @get:Nested
    public val execEnvironments: ListProperty<ExecEnvironment>

    /**
     * Adds [other] environment to this [ExecEnvironment].
     */
    @IgnorableReturnValue
    public fun addEnvironment(other: ExecEnvironment): ExtendableExecEnvironment = apply {
        execEnvironments.add(other)
    }

    /**
     * Adds [others] environments to this [ExecEnvironment].
     */
    @IgnorableReturnValue
    public fun addEnvironments(vararg others: ExecEnvironment): ExtendableExecEnvironment =
        apply {
            execEnvironments.addAll(*others)
        }

    /**
     * Adds [other]'s environment to this [ExecEnvironment].
     */
    @IgnorableReturnValue
    public fun addEnvironment(other: HasExecEnvironment): ExtendableExecEnvironment = apply {
        execEnvironments.add(other.execEnvironment)
    }

    /**
     * Adds [others]'s environments to this [ExecEnvironment].
     */
    @IgnorableReturnValue
    public fun addEnvironments(vararg others: HasExecEnvironment): ExtendableExecEnvironment =
        apply {
            execEnvironments.addAll(others.map { it.execEnvironment })
        }

    /**
     * Adds [other] environment to this [ExecEnvironment] and returns `this`.
     */
    @IgnorableReturnValue
    public operator fun plus(other: ExecEnvironment): ExtendableExecEnvironment {
        return addEnvironment(other)
    }

    /**
     * Adds [other] environment to this [ExecEnvironment].
     */
    public operator fun plusAssign(other: ExecEnvironment) {
        addEnvironment(other)
    }

    /**
     * Adds [other]'s environment to this [ExecEnvironment] and returns `this`.
     */
    @IgnorableReturnValue
    public operator fun plus(other: HasExecEnvironment): ExtendableExecEnvironment {
        return addEnvironment(other)
    }

    /**
     * Adds [other]'s environment to this [ExecEnvironment].
     */
    public operator fun plusAssign(other: HasExecEnvironment) {
        addEnvironment(other)
    }
}