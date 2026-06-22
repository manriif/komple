package komple.task.extract

import komple.task.OutputToolTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * Base for extraction task.
 */
public abstract class ExtractTask : OutputToolTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public abstract val inputDirectory: DirectoryProperty
}