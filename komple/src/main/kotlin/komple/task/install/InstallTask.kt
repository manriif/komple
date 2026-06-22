package komple.task.install

import komple.tool.task.InstallTaskContext
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import javax.inject.Inject

/**
 * Base for install task.
 */
public abstract class InstallTask : DefaultTask() {

    @get:Inject
    protected abstract val fileOperations: FileSystemOperations

    @get:Nested
    public abstract val context: Property<InstallTaskContext>
}