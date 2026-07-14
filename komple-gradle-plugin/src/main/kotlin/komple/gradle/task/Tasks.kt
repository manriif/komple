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
package komple.gradle.task

import komple.gradle.kompleChecksumsDirectory
import komple.gradle.util.dashCased
import komple.task.TaskStateTracker
import komple.task.disableTracking
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Returns a file that can be used to store a checksum for task generated output(s).
 */
private fun Project.checksumFile(taskName: String): Directory {
    var taskUniqueName = taskName
    var parentProject: Project? = this

    while (parentProject != null) {
        taskUniqueName = "${parentProject.name}-$taskUniqueName"
        parentProject = parentProject.parent
    }

    return gradle.kompleChecksumsDirectory.dir(taskUniqueName.lowercase())
}

/**
 * Configures this task and invokes [configure], passing it a [TaskStateTracker].
 */
public fun <T : Task> T.track(configure: T.(tracker: TaskStateTracker) -> Unit) {
    val checksumDirectory = project.checksumFile(name.dashCased())
    val tracker = DefaultTaskStateTracker(checksumDirectory, logger, project.objects)

    tracker.disableTracking()
    configure(this, tracker)

    tracker.inputs = inputs
    tracker.outputFiles = outputs.files

    doLast {
        tracker.updateChecksums()
    }
}

/**
 * Registers tool task and applies default configuration.
 */
internal fun <T : Task> TaskContainer.registerKompleTask(
    name: String,
    type: KClass<T>,
    cacheable: Boolean,
    configure: T.(context: TaskStateTracker) -> Unit,
): TaskProvider<T> = register(name, type.java) {
    if (cacheable) {
        outputs.cacheIf { true }
    }

    track(configure)
}