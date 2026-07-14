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

import komple.gradle.tool.KompleToolConfigContext
import komple.tool.extension.KompleToolExtension
import komple.tool.task.IntegrityTaskContext
import komple.tool.task.IntegrityTaskRegistrationScope
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

/**
 * Default implementation of [IntegrityTaskRegistrationScope].
 */
internal class DefaultIntegrityTaskRegistrationScope<Extension : KompleToolExtension>(
    context: KompleToolConfigContext<Extension>,
    private val downloadTask: TaskProvider<*>
) : IntegrityTaskRegistrationScope<Extension>,
    DefaultTaskRegistrationScope<Extension, IntegrityTaskContext>(context) {

    override val taskPostfix: String
        get() = TASK_TOOL_INTEGRITY_POSTFIX

    override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(context: IntegrityTaskContext) -> Unit
    ): TaskProvider<T> = registerToolTask(klass, cacheable) {
        description = "Check the $toolName tool's integrity."
        val downloadDirectory = downloadTask.outputDirectory(project.layout)

        configure(DefaultIntegrityTaskContext(
            inputDirectory = downloadDirectory,
            execEnvironmentProvider = context.execEnvironmentProvider
        ))

        check(outputs.files.isEmpty) {
            "Integrity task must not register outputs file(s)"
        }

        outputs.dir(downloadDirectory)
    }

    override fun skip(): TaskProvider<*> = downloadTask
}