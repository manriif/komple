package komple.tool.task

import komple.exec.CommandExecutor
import komple.exec.HasCommandExecutorProvider
import komple.platform.HasHost
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

/**
 * Context for task that is expected to produce output files.
 */
public interface TaskContext {

    /**
     * The directory that must be used to write task produced file(s).
     * The task output directory is used as input directory for the next task.
     */
    public val outputDirectory: Directory

    /**
     * Indicates whether there is changes in output files since last task execution.
     *
     * Gradle may re-execute `up-to-date` task, under some circumstances like change in build-logic
     * script(s), even if inputs and outputs did not change.
     *
     * Komple adds its own snapshot system on top of Gradle's one.
     *
     * This property can be used to detect changes in output files. If the provided value is `true`,
     * then it is recommended to skip task action execution.
     *
     * This is provided to avoid large files being re-downloaded or re-extracted and long-running
     * installation being performed unnecessarily.
     */
    public val outputChanged: Provider<Boolean>
}

/**
 * Task context providing access to [CommandExecutor].
 */
public interface ExecTaskContext : TaskContext, HasHost, HasCommandExecutorProvider

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Invokes [action] only if `this` [TaskContext.outputChanged] provides `true` when read.
 */
public inline fun TaskContext.whenOutputChanged(action: () -> Unit) {
    if (outputChanged.get()) {
        action()
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskContext.outputChanged]
 * provides `true` when read.
 */
public inline fun Task.doFirstWhenOutputChanged(
    context: TaskContext,
    crossinline action: () -> Unit
) {
    doFirst {
        context.whenOutputChanged(action)
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskContext.outputChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doFirstWhenOutputChangedKt")
context(task: Task)
public inline fun TaskContext.doFirstWhenOutputChanged(crossinline action: () -> Unit) {
    task.doFirstWhenOutputChanged(this, action)
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskContext.outputChanged]
 * provides `true` when read.
 */
public inline fun Task.doLastWhenOutputChanged(
    context: TaskContext,
    crossinline action: () -> Unit
) {
    doLast {
        context.whenOutputChanged(action)
    }
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskContext.outputChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doLastWhenOutputChangedKt")
context(task: Task)
public inline fun TaskContext.doLastWhenOutputChanged(crossinline action: () -> Unit) {
    task.doLastWhenOutputChanged(this, action)
}