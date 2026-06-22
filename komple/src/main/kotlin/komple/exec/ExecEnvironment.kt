package komple.exec

import org.gradle.process.ExecOperations

/**
 * [ShellEnvironment] that can be transformed into [CommandExecutor].
 */
public interface ExecEnvironment : ShellEnvironment {

    /**
     * Returns a new [CommandExecutor.Factory].
     */
    public fun createCommandExecutorFactory(): CommandExecutor.Factory
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a new [CommandExecutor] wrapping [execOperations].
 */
public fun ExecEnvironment.createCommandExecutor(execOperations: ExecOperations): CommandExecutor =
    createCommandExecutorFactory().create(execOperations)