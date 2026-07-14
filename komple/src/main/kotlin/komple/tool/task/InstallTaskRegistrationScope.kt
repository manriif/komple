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
package komple.tool.task

import komple.task.enableTracking
import komple.task.install.CommandInstallTask
import komple.task.install.DefaultCommandInstallTask
import komple.task.install.InstallCommandProvider
import komple.task.install.InstallTask
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Scope for install task registration.
 */
public interface InstallTaskRegistrationScope<Extension : KompleToolExtension> :
    TaskRegistrationScope<Extension, InstallTaskContext> {

    /**
     * Registers a task of type [T], [configure]s it and returns it.
     *
     * The directory where extracted (or downloaded, if extraction was skipped) file(s) lives and
     * where installed file(s) must be written to can be obtained from the
     * [InstallTaskContext.inputDirectory] and [InstallTaskContext.outputDirectory] passed to
     * [configure]. The input directory should eventually be registered as task's input. The output
     * directory must be registered as the task's only output directory.
     */
    public override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(context: InstallTaskContext) -> Unit
    ): TaskProvider<T>

    /**
     * Registers a task that skips installation, causing extracted (or downloaded, if extraction was
     * skipped) file(s) to be used as installed files.
     */
    override fun skip(): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T], [configure]s it and returns it.
 *
 * The directory where extracted (or downloaded, if extraction was skipped) file(s) lives and
 * where installed file(s) must be written to can be obtained from the
 * [InstallTaskContext.inputDirectory] and [InstallTaskContext.outputDirectory] passed to
 * [configure]. The input directory should eventually be registered as task's input. The output
 * directory must be registered as the task's only output directory.
 */
public inline fun <reified T : Task> InstallTaskRegistrationScope<*>.register(
    cacheable: Boolean = false,
    noinline configure: T.(context: InstallTaskContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    cacheable = cacheable,
    configure = configure
)

/**
 * Registers a task of type [T] subclass of [InstallTask] and [configure]s it.
 *
 * The [InstallTask.execEnvironment], [InstallTask.tracker], [InstallTask.outputDirectory] and
 * [InstallTask.inputDirectory] properties are configured before [configure] is invoked.
 *
 * Tracking is enabled by default.
 */
public inline fun <reified T : InstallTask> InstallTaskRegistrationScope<*>.install(
    cacheable: Boolean = false,
    noinline configure: (T.(context: InstallTaskContext) -> Unit)? = null
): TaskProvider<T> = outputTool(cacheable) { context ->
    this.inputDirectory = context.inputDirectory
    context.tracker.enableTracking()
    configure?.invoke(this, context)
}

/**
 * Registers a task of type [T] subclass of [CommandInstallTask] and [configure]s it.
 *
 * The [CommandInstallTask.execEnvironment], [CommandInstallTask.tracker],
 * [CommandInstallTask.outputDirectory] and [CommandInstallTask.inputDirectory] properties are
 * configured before [configure] is invoked.
 *
 * Tracking is enabled by default.
 */
public inline fun <reified T : CommandInstallTask> InstallTaskRegistrationScope<*>.command(
    cacheable: Boolean = false,
    noinline configure: (T.(context: InstallTaskContext) -> Unit)? = null
): TaskProvider<T> = install<T>(cacheable, configure)

/**
 * Registers a task configuring tool files using a command obtained via [provider].
 *
 * Tracking is enabled by default.
 * Note that the returned task is cacheable.
 */
public fun InstallTaskRegistrationScope<*>.command(
    provider: InstallCommandProvider
): TaskProvider<DefaultCommandInstallTask> = command(false) {
    this.provider = provider
}