/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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