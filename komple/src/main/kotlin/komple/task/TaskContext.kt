package komple.task

import org.gradle.api.file.Directory

/**
 * Context for a task.
 */
public interface TaskContext {

    /**
     * The directory that should optionally be used to output task file(s).
     * Anyway, the task output files are used as input to the forward step.
     */
    public val outputDirectory: Directory
}