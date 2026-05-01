package komple.gradle.task

import komple.task.TaskContext
import org.gradle.api.provider.Provider

/**
 * Default implementation of [TaskContext].
 */
internal class DefaultTaskContext(override val outputChanged: Provider<Boolean>) : TaskContext