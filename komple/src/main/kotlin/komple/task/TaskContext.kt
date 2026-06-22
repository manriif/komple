package komple.task

import org.gradle.api.Task

/**
 * Context for Gradle task.
 *
 * Gradle may re-execute `up-to-date` task, under some circumstances like change in build-logic
 * script(s), even if inputs and outputs did not change.
 *
 * Komple adds its own snapshot system on top of Gradle's one. The system is optional and is
 * provided to avoid large files being re-downloaded or re-extracted and long-running installation
 * being performed unnecessarily.
 */
public interface TaskContext {

    /**
     * Returns `true` if the declared task inputs changed, `false` otherwise.
     */
    public fun hasInputsChanged(): Boolean

    /**
     * Returns `true` if the task output files changed, `false` otherwise.
     */
    public fun hasOutputsChanged(): Boolean
}

///////////////////////////////////////////////////////////////////////////
// Inputs
///////////////////////////////////////////////////////////////////////////

/**
 * Invokes [action] only if `this` [TaskContext.hasInputsChanged] provides `true` when read.
 */
public inline fun TaskContext.whenInputsChanged(action: () -> Unit) {
    if (hasInputsChanged()) {
        action()
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskContext.hasInputsChanged]
 * provides `true` when read.
 */
public inline fun Task.doFirstWhenInputsChanged(
    context: TaskContext,
    crossinline action: () -> Unit
) {
    doFirst {
        context.whenInputsChanged(action)
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskContext.hasInputsChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doFirstWhenInputsChangedKt")
context(task: Task)
public inline fun TaskContext.doFirstWhenInputsChanged(crossinline action: () -> Unit) {
    task.doFirstWhenInputsChanged(this, action)
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskContext.hasInputsChanged]
 * provides `true` when read.
 */
public inline fun Task.doLastWhenInputsChanged(
    context: TaskContext,
    crossinline action: () -> Unit
) {
    doLast {
        context.whenInputsChanged(action)
    }
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskContext.hasInputsChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doLastWhenInputsChangedKt")
context(task: Task)
public inline fun TaskContext.doLastWhenInputsChanged(crossinline action: () -> Unit) {
    task.doLastWhenInputsChanged(this, action)
}

///////////////////////////////////////////////////////////////////////////
// Outputs
///////////////////////////////////////////////////////////////////////////

/**
 * Invokes [action] only if `this` [TaskContext.hasOutputsChanged] provides `true` when read.
 */
public inline fun TaskContext.whenOutputsChanged(action: () -> Unit) {
    if (hasOutputsChanged()) {
        action()
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskContext.hasOutputsChanged]
 * provides `true` when read.
 */
public inline fun Task.doFirstWhenOutputsChanged(
    context: TaskContext,
    crossinline action: () -> Unit
) {
    doFirst {
        context.whenOutputsChanged(action)
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskContext.hasOutputsChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doFirstWhenOutputsChangedKt")
context(task: Task)
public inline fun TaskContext.doFirstWhenOutputsChanged(crossinline action: () -> Unit) {
    task.doFirstWhenOutputsChanged(this, action)
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskContext.hasOutputsChanged]
 * provides `true` when read.
 */
public inline fun Task.doLastWhenOutputsChanged(
    context: TaskContext,
    crossinline action: () -> Unit
) {
    doLast {
        context.whenOutputsChanged(action)
    }
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskContext.hasOutputsChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doLastWhenOutputsChangedKt")
context(task: Task)
public inline fun TaskContext.doLastWhenOutputsChanged(crossinline action: () -> Unit) {
    task.doLastWhenOutputsChanged(this, action)
}

///////////////////////////////////////////////////////////////////////////
// Inputs + Outputs
///////////////////////////////////////////////////////////////////////////

/**
 * Returns `true` if either the declared task inputs or output files changed, `false` otherwise.
 */
public fun TaskContext.hasChanged(): Boolean = hasInputsChanged() || hasOutputsChanged()

/**
 * Invokes [action] only if `this` [TaskContext.hasChanged] provides `true` when read.
 */
public inline fun TaskContext.whenChanged(action: () -> Unit) {
    if (hasChanged()) {
        action()
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskContext.hasChanged]
 * provides `true` when read.
 */
public inline fun Task.doFirstWhenChanged(
    context: TaskContext,
    crossinline action: () -> Unit
) {
    doFirst {
        context.whenChanged(action)
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskContext.hasChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doFirstWhenChangedKt")
context(task: Task)
public inline fun TaskContext.doFirstWhenChanged(crossinline action: () -> Unit) {
    task.doFirstWhenChanged(this, action)
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskContext.hasChanged]
 * provides `true` when read.
 */
public inline fun Task.doLastWhenChanged(
    context: TaskContext,
    crossinline action: () -> Unit
) {
    doLast {
        context.whenChanged(action)
    }
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskContext.hasChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doLastWhenChangedKt")
context(task: Task)
public inline fun TaskContext.doLastWhenChanged(crossinline action: () -> Unit) {
    task.doLastWhenChanged(this, action)
}