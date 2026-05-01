package komple.gradle.tool.task

import komple.exec.ExecEnvironment
import komple.gradle.platform.CurrentHost
import komple.platform.Host
import komple.task.TaskContext
import komple.tool.task.DownloadTaskContext
import komple.tool.task.ExecToolTaskContext
import komple.tool.task.ExtractTaskContext
import komple.tool.task.InstallTaskContext
import komple.tool.task.TaskDirectory
import komple.tool.task.ToolTaskContext
import org.gradle.api.file.Directory

/**
 * Base for implementors of [ToolTaskContext].
 */
internal abstract class DefaultTaskContext(
    context: TaskContext,
    override val outputDirectory: Directory,
) : ToolTaskContext,
    TaskContext by context

/**
 * Default implementation of [DownloadTaskContext].
 */
internal class DefaultDownloadTaskContext(
    context: TaskContext,
    outputDirectory: Directory
) : DownloadTaskContext,
    DefaultTaskContext(
        context = context,
        outputDirectory = outputDirectory
    )

/**
 * Default implementation of [ExtractTaskContext].
 */
internal abstract class DefaultExecTaskContext(
    context: TaskContext,
    override val execEnvironment: ExecEnvironment,
    outputDirectory: Directory
) : ExecToolTaskContext,
    DefaultTaskContext(
        context = context,
        outputDirectory = outputDirectory
    ) {

    override val host: Host
        get() = CurrentHost
}

/**
 * Default implementation of [ExtractTaskContext].
 */
internal class DefaultExtractTaskContext(
    context: TaskContext,
    override val downloadDirectory: TaskDirectory,
    execEnvironment: ExecEnvironment,
    outputDirectory: Directory
) : ExtractTaskContext,
    DefaultExecTaskContext(
        context = context,
        execEnvironment = execEnvironment,
        outputDirectory = outputDirectory
    )

/**
 * Default implementation of [InstallTaskContext].
 */
internal class DefaultInstallTaskContext(
    context: TaskContext,
    override val extractDirectory: TaskDirectory,
    execEnvironment: ExecEnvironment,
    outputDirectory: Directory
) : InstallTaskContext,
    DefaultExecTaskContext(
        context = context,
        execEnvironment = execEnvironment,
        outputDirectory = outputDirectory
    )