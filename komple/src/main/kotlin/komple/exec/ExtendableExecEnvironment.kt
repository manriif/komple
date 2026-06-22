package komple.exec

import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Nested

/**
 * [ExecEnvironment] that can be extended with other [ShellEnvironment]s.
 */
public interface ExtendableExecEnvironment : ExecEnvironment {

    /**
     * Extras [ShellEnvironment]s.
     */
    @get:Nested
    public val shellEnvironments: ListProperty<ShellEnvironment>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Adds [other] environment to this [ExtendableExecEnvironment].
 */
@IgnorableReturnValue
public fun ExtendableExecEnvironment.addEnvironment(other: ShellEnvironment) {
    shellEnvironments.add(other)
}

/**
 * Adds [others] environments to this [ExtendableExecEnvironment].
 */
@IgnorableReturnValue
public fun ExtendableExecEnvironment.addEnvironments(vararg others: ShellEnvironment) {
    shellEnvironments.addAll(*others)
}

/**
 * Adds [other]'s environment to this [ExtendableExecEnvironment].
 */
@IgnorableReturnValue
public fun ExtendableExecEnvironment.addEnvironment(other: HasShellEnvironment) {
    shellEnvironments.add(other.shellEnvironment)
}

/**
 * Adds [others]'s environments to this [ExtendableExecEnvironment].
 */
@IgnorableReturnValue
public fun ExtendableExecEnvironment.addEnvironments(vararg others: HasShellEnvironment) {
    shellEnvironments.addAll(others.map { it.shellEnvironment })
}

/**
 * Adds [other] environment to this [ExtendableExecEnvironment].
 */
@IgnorableReturnValue
public operator fun ExtendableExecEnvironment.plus(other: ShellEnvironment) {
    addEnvironment(other)
}

/**
 * Adds [other] environment to this [ExtendableExecEnvironment].
 */
public operator fun ExtendableExecEnvironment.plusAssign(other: ShellEnvironment) {
    addEnvironment(other)
}

/**
 * Adds [other]'s environment to this [ExtendableExecEnvironment].
 */
@IgnorableReturnValue
public operator fun ExtendableExecEnvironment.plus(other: HasShellEnvironment) {
    addEnvironment(other)
}

/**
 * Adds [other]'s environment to this [ExtendableExecEnvironment].
 */
public operator fun ExtendableExecEnvironment.plusAssign(other: HasShellEnvironment) {
    addEnvironment(other)
}