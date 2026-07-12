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
package komple.project

import komple.exec.ExecEnvironment
import komple.exec.HasExecEnvironment
import komple.exec.KompleExecTask
import komple.platform.HasHost
import komple.task.TaskStateTracker
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
     * Execution environment for which tool environment configuration is applied to.
     */
    override val execEnvironment: ExecEnvironment

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
     * Creates a task of type [T], named after the project name  and [postfix] appended.
     *
     * The returned task can be configured inside [configure].
     */
    @IgnorableReturnValue
    public fun <T : Task> registerTask(
        postfix: String,
        type: KClass<T>,
        cacheable: Boolean = false,
        configure: (T.(tracker: TaskStateTracker) -> Unit)? = null
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
 * Creates a task of type [T], named after the project name and [postfix] appended.
 *
 * The returned task can be configured inside [configure].
 */
@IgnorableReturnValue
public inline fun <reified T : Task> ProjectConfigurationScope<*>.registerTask(
    postfix: String,
    cacheable: Boolean = false,
    noinline configure: (T.(tracker: TaskStateTracker) -> Unit)? = null
): TaskProvider<T> = registerTask(
    postfix = postfix,
    type = T::class,
    cacheable = cacheable,
    configure = configure
)

/**
 * Creates a task of type [T], named after the project name and [postfix] appended.
 *
 * The [KompleExecTask.execEnvironment] is configured before [configure] is invoked to configure the
 * task [T].
 */
@IgnorableReturnValue
public inline fun <reified T : KompleExecTask> ProjectConfigurationScope<*>.registerExecTask(
    postfix: String,
    cacheable: Boolean = false,
    noinline configure: (T.(tracker: TaskStateTracker) -> Unit)? = null
): TaskProvider<T> = registerTask<T>(postfix, cacheable) { context ->
    this.execEnvironment = this@registerExecTask.execEnvironment
    configure?.invoke(this, context)
}