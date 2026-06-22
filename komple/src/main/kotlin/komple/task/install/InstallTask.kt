package komple.task.install

import komple.task.OutputToolTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

/**
 * Base for install task.
 */
public abstract class InstallTask : OutputToolTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public abstract val inputDirectory: DirectoryProperty
}