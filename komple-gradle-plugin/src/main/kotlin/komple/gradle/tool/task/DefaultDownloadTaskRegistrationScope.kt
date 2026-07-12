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

import komple.gradle.kompleToolsDownloadsDirectory
import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.DownloadTaskContext
import komple.tool.task.DownloadTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [DownloadTaskRegistrationScope].
 */
internal class DefaultDownloadTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>
) : DownloadTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension, DownloadTaskContext>(context) {

    override val taskPostfix: String
        get() = TASK_TOOL_DOWNLOAD_POSTFIX

    override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(DownloadTaskContext) -> Unit
    ): TaskProvider<T> = registerToolTask(klass, cacheable) { tracker ->
        description = "Download the $toolName tool."

        configure(
            DefaultDownloadTaskContext(
                tracker = tracker,
                outputDirectory = project.gradle.kompleToolsDownloadsDirectory.dir(toolNameCompat),
                execEnvironmentProvider = context.execEnvironmentProvider
            )
        )
    }

    override fun skip(): TaskProvider<*> =
        registerFailureTask(UnsupportedOperationException("No file to download for tool $toolName"))
}