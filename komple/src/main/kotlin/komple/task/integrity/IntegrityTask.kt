package komple.task.integrity

import komple.task.ToolTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * Base for integrity task.
 */
public abstract class IntegrityTask : ToolTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public abstract val inputDirectory: DirectoryProperty
}