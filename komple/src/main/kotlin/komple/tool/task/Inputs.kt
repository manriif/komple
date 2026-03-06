package komple.tool.task

import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * Represents input file(s) to a task.
 */
public interface Inputs {

    /**
     * Assumes that only a single [Directory] was produced by previous task.
     */
    public val directory: Provider<Directory>

    /**
     * Assumes that only a single [RegularFile] was produced by previous task.
     */
    public val file: Provider<RegularFile>

    /**
     * Returns the previous task produced files.
     */
    public val files: Provider<FileCollection>
}