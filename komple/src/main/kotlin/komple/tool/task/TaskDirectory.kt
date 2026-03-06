package komple.tool.task

import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * Represents a directory where task related file(s) resides.
 * Files belonging to the represented directory usually comes from another task.
 */
public interface TaskDirectory {

    /**
     * Returns this task
     */
    public val directory: Provider<Directory>

    /**
     * Assumes that only a single [RegularFile] is present in the directory and returns it.
     */
    public val singleFile: Provider<RegularFile>
}