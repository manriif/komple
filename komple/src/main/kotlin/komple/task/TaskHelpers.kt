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