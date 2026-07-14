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

import komple.gradle.task.registerKompleTask
import komple.gradle.util.camelCased
import komple.task.TaskStateTracker
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

internal const val KOMPLE_TOOLS_TASK_GROUP = "komple tools"
internal const val TASK_TOOL_DOWNLOAD_POSTFIX = "Download"
internal const val TASK_TOOL_INTEGRITY_POSTFIX = "Integrity"
internal const val TASK_TOOL_EXTRACT_POSTFIX = "Extract"
internal const val TASK_TOOL_INSTALL_POSTFIX = "Install"

///////////////////////////////////////////////////////////////////////////
// Registration
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a conventional name for a task and for the tool.
 */
internal fun toolTaskName(toolName: String, postfix: String): String =
    "${toolName.camelCased()}$postfix"

/**
 * Registers tool task and applies default configuration.
 */
internal fun <T : Task> TaskContainer.registerToolTask(
    name: String,
    type: KClass<T>,
    cacheable: Boolean,
    configure: T.(context: TaskStateTracker) -> Unit
): TaskProvider<T> = registerKompleTask(name, type, cacheable) { context ->
    group = KOMPLE_TOOLS_TASK_GROUP
    configure(this, context)
}

///////////////////////////////////////////////////////////////////////////
// Output
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a [Provider] to the output file of the task as a single directory.
 */
internal fun Provider<out Task>.outputDirectory(layout: ProjectLayout): Provider<Directory> =
    layout.dir(map { it.outputs.files.singleFile })