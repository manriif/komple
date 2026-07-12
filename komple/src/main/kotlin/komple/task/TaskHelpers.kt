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
package komple.task

import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import java.io.File
import java.io.FileNotFoundException

/**
 * Returns the single file present in this directory.
 */
public val File.singleFile: File
    get() = listFiles()
        ?.singleOrNull(File::isFile)
        ?: throw FileNotFoundException(
            "Either the file is not a directory or it contains no file, more than one file " +
                    "or the only present file is a directory"
        )

/**
 * Returns a [Provider] resolving the single file present in the resolved directory.
 */
public val Provider<Directory>.singleFile: Provider<RegularFile>
    get() = map { it.file(it.asFile.singleFile.name) }

/**
 * Deletes [directory] and its content and returns the [File] to the [directory].
 */
public fun FileSystemOperations.clearAndGetAsFile(directory: Provider<Directory>): File {
    return directory.get().asFile.also { directory ->
        delete { delete(directory) }
        directory.mkdirs()
    }
}

/**
 * Disables caching for the task provided by [provider].
 */
public fun noCache(provider: TaskProvider<out Task>): TaskProvider<*> {
    provider.configure {
        outputs.cacheIf { false }
    }

    return provider
}

/**
 * Disables caching for the task provided by [provider].
 */
public inline fun <R> R.noCache(
    provider: R.() -> TaskProvider<*>
): TaskProvider<*> = noCache(provider())