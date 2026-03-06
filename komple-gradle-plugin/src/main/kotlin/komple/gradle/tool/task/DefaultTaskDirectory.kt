package komple.gradle.tool.task

import komple.tool.task.TaskDirectory
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import java.io.File
import java.io.FileNotFoundException

/**
 * Default implementation of [TaskDirectory].
 */
internal class DefaultTaskDirectory(override val directory: Provider<Directory>) : TaskDirectory {

    override val singleFile: Provider<RegularFile>
        get() = directory.map { directory ->
            val file = directory.asFile
                .listFiles()
                .singleOrNull(File::isFile)
                ?: throw FileNotFoundException(
                    "The directory contains no file, more than one file or the file present is a" +
                            " directory"
                )

            directory.file(file.name)
        }
}