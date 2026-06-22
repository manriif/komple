package komple.task

import komple.exec.KompleExecTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import javax.inject.Inject

/**
 * Base for tool task.
 */
public abstract class ToolTask : KompleExecTask()

/**
 * Base for tool task that produces output.
 */
public abstract class OutputToolTask : ToolTask() {

    @get:Inject
    protected abstract val fileOperations: FileSystemOperations

    @get:Internal
    public abstract val tracker: Property<TaskStateTracker>

    @get:OutputDirectory
    public abstract val outputDirectory: DirectoryProperty
}

