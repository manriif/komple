package komple.task

import org.gradle.api.Task

/**
 * Tracker for Task input and outputs.
 *
 * Gradle may re-execute `up-to-date` task, under some circumstances like change in build-logic
 * script(s), even if inputs and outputs had not changed.
 *
 * Komple adds its own snapshot system on top of Gradle's one. The system is optional and is
 * provided to avoid large files being re-downloaded or re-extracted and long-running installation
 * being performed unnecessarily.
 *
 * Note that the tracked inputs types only includes:
 * - Primitives ([Long], [Int], [Short], [Byte], [Double], [Float])
 * - [String]
 * - [Enum]
 * - [Serializable]
 * - [java.io.File]
 * - [org.gradle.api.file.FileCollection]
 * - [Collection] where elements are of above types
 * - [Map] where key and values of above types
 *
 * Any other input type is skipped.
 */
public interface TaskStateTracker {

    /**
     * Whether inputs should be tracked.
     */
    public var isInputTrackingEnabled: Boolean

    /**
     * Whether outputs should be tracked.
     */
    public var isOutputTrackingEnabled: Boolean

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
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Enables both inputs and outputs tracking.
 */
public fun TaskStateTracker.enableTracking() {
    isInputTrackingEnabled = true
    isOutputTrackingEnabled = true
}

/**
 * Disables both inputs and outputs tracking.
 */
public fun TaskStateTracker.disableTracking() {
    isInputTrackingEnabled = false
    isOutputTrackingEnabled = false
}

///////////////////////////////////////////////////////////////////////////
// Inputs
///////////////////////////////////////////////////////////////////////////

/**
 * Invokes [action] only if `this` [TaskStateTracker.hasInputsChanged] provides `true` when read.
 */
public inline fun TaskStateTracker.whenInputsChanged(action: () -> Unit) {
    if (hasInputsChanged()) {
        action()
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskStateTracker.hasInputsChanged]
 * provides `true` when read.
 */
public inline fun Task.doFirstWhenInputsChanged(
    tracker: TaskStateTracker,
    crossinline action: () -> Unit
) {
    doFirst {
        tracker.whenInputsChanged(action)
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskStateTracker.hasInputsChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doFirstWhenInputsChangedKt")
context(task: Task)
public inline fun TaskStateTracker.doFirstWhenInputsChanged(crossinline action: () -> Unit) {
    task.doFirstWhenInputsChanged(this, action)
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskStateTracker.hasInputsChanged]
 * provides `true` when read.
 */
public inline fun Task.doLastWhenInputsChanged(
    tracker: TaskStateTracker,
    crossinline action: () -> Unit
) {
    doLast {
        tracker.whenInputsChanged(action)
    }
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskStateTracker.hasInputsChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doLastWhenInputsChangedKt")
context(task: Task)
public inline fun TaskStateTracker.doLastWhenInputsChanged(crossinline action: () -> Unit) {
    task.doLastWhenInputsChanged(this, action)
}

///////////////////////////////////////////////////////////////////////////
// Outputs
///////////////////////////////////////////////////////////////////////////

/**
 * Invokes [action] only if `this` [TaskStateTracker.hasOutputsChanged] provides `true` when read.
 */
public inline fun TaskStateTracker.whenOutputsChanged(action: () -> Unit) {
    if (hasOutputsChanged()) {
        action()
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskStateTracker.hasOutputsChanged]
 * provides `true` when read.
 */
public inline fun Task.doFirstWhenOutputsChanged(
    tracker: TaskStateTracker,
    crossinline action: () -> Unit
) {
    doFirst {
        tracker.whenOutputsChanged(action)
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskStateTracker.hasOutputsChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doFirstWhenOutputsChangedKt")
context(task: Task)
public inline fun TaskStateTracker.doFirstWhenOutputsChanged(crossinline action: () -> Unit) {
    task.doFirstWhenOutputsChanged(this, action)
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskStateTracker.hasOutputsChanged]
 * provides `true` when read.
 */
public inline fun Task.doLastWhenOutputsChanged(
    tracker: TaskStateTracker,
    crossinline action: () -> Unit
) {
    doLast {
        tracker.whenOutputsChanged(action)
    }
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskStateTracker.hasOutputsChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doLastWhenOutputsChangedKt")
context(task: Task)
public inline fun TaskStateTracker.doLastWhenOutputsChanged(crossinline action: () -> Unit) {
    task.doLastWhenOutputsChanged(this, action)
}

///////////////////////////////////////////////////////////////////////////
// Inputs + Outputs
///////////////////////////////////////////////////////////////////////////

/**
 * Returns `true` if either the declared task inputs or output files changed, `false` otherwise.
 */
public fun TaskStateTracker.hasChanged(): Boolean = hasInputsChanged() || hasOutputsChanged()

/**
 * Invokes [action] only if `this` [TaskStateTracker.hasChanged] provides `true` when read.
 */
public inline fun TaskStateTracker.whenChanged(action: () -> Unit) {
    if (hasChanged()) {
        action()
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskStateTracker.hasChanged]
 * provides `true` when read.
 */
public inline fun Task.doFirstWhenChanged(
    tracker: TaskStateTracker,
    crossinline action: () -> Unit
) {
    doFirst {
        tracker.whenChanged(action)
    }
}

/**
 * Registers a [Task.doFirst] action invoking [action] only if `this` [TaskStateTracker.hasChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doFirstWhenChangedKt")
context(task: Task)
public inline fun TaskStateTracker.doFirstWhenChanged(crossinline action: () -> Unit) {
    task.doFirstWhenChanged(this, action)
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskStateTracker.hasChanged]
 * provides `true` when read.
 */
public inline fun Task.doLastWhenChanged(
    tracker: TaskStateTracker,
    crossinline action: () -> Unit
) {
    doLast {
        tracker.whenChanged(action)
    }
}

/**
 * Registers a [Task.doLast] action invoking [action] only if `this` [TaskStateTracker.hasChanged]
 * provides `true` when read.
 */
@JvmSynthetic
@JvmName("doLastWhenChangedKt")
context(task: Task)
public inline fun TaskStateTracker.doLastWhenChanged(crossinline action: () -> Unit) {
    task.doLastWhenChanged(this, action)
}