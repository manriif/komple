package komple.task.integrity

import komple.tool.task.TaskDirectory
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * Base for integrity task.
 */
public abstract class IntegrityTask : DefaultTask() {

    @get:Nested
    public abstract val inputDirectory: Property<TaskDirectory>
}