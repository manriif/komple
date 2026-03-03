package komple.tool.install

/**
 * Opaque state of a task.
 */
public interface TaskState {

    /**
     * Invokes [execute] only if there is changes in output files since last task execution.
     *
     * The used changes detection mechanism is independent of the Gradle's one which may be skipped
     * under some circumstances like changes in build logic scripts.
     *
     * This can be used on any task, but it makes much sense for long-running tasks.
     */
    public fun executeIfOutputFilesChanged(execute: () -> Unit)
}