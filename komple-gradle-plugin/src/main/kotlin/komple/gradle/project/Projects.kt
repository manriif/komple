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
package komple.gradle.project

import komple.gradle.task.registerKompleTask
import komple.gradle.util.camelCased
import komple.gradle.util.dashCased
import komple.gradle.util.pascalCased
import komple.task.TaskStateTracker
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

private const val KOMPLE_PROJECTS_TASK_GROUP = "komple projects"

/**
 * Returns a directory where to store project generated files.
 */
internal fun ProjectLayout.projectGeneratedOutputDir(
    projectName: String,
    subdirectory: String
): Provider<Directory> {
    return buildDirectory.dir("generated/komple/projects/${projectName.dashCased()}/$subdirectory")
}

///////////////////////////////////////////////////////////////////////////
// Task
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a derived name for [projectName] including [postfix].
 */
internal fun projectDerivedName(projectName: String, postfix: String): String {
    return "${projectName.camelCased()}${postfix.pascalCased()}"
}

/**
 * Registers a project task and applies default configuration.
 */
internal fun <T : Task> TaskContainer.registerProjectTask(
    name: String,
    type: KClass<T>,
    cacheable: Boolean = false,
    configure: (T.(context: TaskStateTracker) -> Unit)? = null
): TaskProvider<T> = registerKompleTask(name, type, cacheable) { context ->
    group = KOMPLE_PROJECTS_TASK_GROUP
    configure?.invoke(this, context)
}

/**
 * Registers a project task and applies default configuration.
 */
internal inline fun <reified T : Task> TaskContainer.registerProjectTask(
    name: String,
    cacheable: Boolean = false,
    noinline configure: (T.(context: TaskStateTracker) -> Unit)? = null
): TaskProvider<T> = registerProjectTask(
    name = name,
    type = T::class,
    cacheable = cacheable,
    configure = configure
)