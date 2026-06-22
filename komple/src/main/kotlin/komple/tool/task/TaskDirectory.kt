package komple.tool.task

import groovy.transform.Internal
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory

/**
 * Represents a directory where task output file(s) resides.
 * Files belonging to the represented directory usually comes from another task.
 */
public interface TaskDirectory {

    /**
     * Returns previous task output directory which is indeed the task input directory.
     */
    @get:InputDirectory
    public val directory: Provider<Directory>

    /**
     * Assumes that only a single [RegularFile] is present in the directory and returns it.
     */
    @get:Internal
    public val singleFile: Provider<RegularFile>
}