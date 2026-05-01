package komple.tool.task

import komple.exec.HasExecEnvironment
import komple.platform.HasHost
import komple.task.TaskContext
import org.gradle.api.file.Directory

/**
 * Context for task that is expected to produce output files.
 */
public interface ToolTaskContext : TaskContext {

    /**
     * The directory that must be used to write task produced file(s).
     * The task output directory is used as input directory for the next task.
     */
    public val outputDirectory: Directory
}

/**
 * Task context providing access to [komple.exec.ExecEnvironment].
 */
public interface ExecToolTaskContext : ToolTaskContext, HasHost, HasExecEnvironment