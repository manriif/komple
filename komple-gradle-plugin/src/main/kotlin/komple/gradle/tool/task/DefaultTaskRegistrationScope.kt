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
package komple.gradle.tool.task

import komple.gradle.platform.configureUnsupportedHost
import komple.gradle.problem.ProblemThrowerTask
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.gradle.util.dashCased
import komple.platform.Host
import komple.task.TaskStateTracker
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import komple.tool.task.TaskRegistrationScope
import komple.tool.task.ToolTaskContext
import org.gradle.api.Task
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default base implementation for [TaskRegistrationScope].
 */
internal abstract class DefaultTaskRegistrationScope<E : KompleToolExtension, C : ToolTaskContext>(
    protected val context: KompleToolConfigContext<E>,
) : TaskRegistrationScope<E, C>,
    HasExtension<E> by context,
    ClosableScope() {

    protected abstract val taskPostfix: String

    override val host: Host
        get() = notClosed { context.host }

    override val providers: ProviderFactory
        get() = notClosed { context.project.providers }

    override val toolName: String
        get() = context.toolName

    override val toolNameCompat: String
        get() = toolName.dashCased()

    protected val toolTaskName: String
        get() = toolTaskName(toolName, taskPostfix)

    /**
     * Registers a task [T] as [toolTaskName].
     */
    protected fun <T : Task> registerToolTask(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(tracker: TaskStateTracker) -> Unit
    ): TaskProvider<T> = context.project.tasks.registerToolTask(
        name = toolTaskName,
        type = klass,
        cacheable = cacheable,
        configure = configure
    )

    /**
     * Registers a task that always fails when executed.
     */
    protected fun registerProblemTask(configure: ProblemThrowerTask.() -> Unit): TaskProvider<*> =
        context.project.tasks.registerToolTask(
            name = toolTaskName,
            type = ProblemThrowerTask::class,
            cacheable = false
        ) {
            configure()
        }

    final override fun unsupported(): TaskProvider<*> =
        registerProblemTask { configureUnsupportedHost(toolName, context.host) }
}