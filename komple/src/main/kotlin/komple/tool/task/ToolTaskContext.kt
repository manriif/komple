package komple.tool.task

import komple.exec.HasExecEnvironment
import komple.platform.HasHost
import komple.task.TaskStateTracker
import org.gradle.api.file.Directory

/**
 * Context for tool task.
 */
public interface ToolTaskContext : HasExecEnvironment, HasHost

/**
 * Context for tool task that is expected to produce output files.
 */
public interface OutputToolTaskContext : ToolTaskContext {

    /**
     * State tracker for the task.
     */
    public val tracker: TaskStateTracker

    /**
     * The directory that must be used to write task produced file(s).
     * The task output directory is used as input directory for the next task.
     */
    public val outputDirectory: Directory
}