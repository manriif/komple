package komple.task.extract

import komple.tool.task.ExtractTaskContext
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import javax.inject.Inject

/**
 * Base for extraction task.
 */
public abstract class ExtractTask : DefaultTask() {

    @get:Inject
    protected abstract val fileOperations: FileSystemOperations

    @get:Nested
    public abstract val context: Property<ExtractTaskContext>
}