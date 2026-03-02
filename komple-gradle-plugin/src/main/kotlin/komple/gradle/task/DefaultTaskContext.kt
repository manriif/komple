package komple.gradle.task

import komple.task.TaskContext
import org.gradle.api.file.Directory

/**
 * Base for implementors of [TaskContext].
 */
internal abstract class DefaultTaskContext(override val outputDirectory: Directory) : TaskContext