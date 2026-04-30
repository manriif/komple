package komple.exec

import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import java.io.File
import java.io.Serializable

/**
 * Gives access to command execution in an environment where registered tools are available.
 */
public interface CommandExecutor {

    /**
     * Executes the [command], in an environment configured by registered tools.
     */
    @IgnorableReturnValue
    public fun execute(
        command: Command,
        workingDirectory: File? = null
    ): ExecResult

    /**
     * Executes the [command], in an environment configured by registered tools and returns the
     * output.
     */
    @IgnorableReturnValue
    public fun executeWithOutput(
        command: Command,
        workingDirectory: File? = null
    ): String

    ///////////////////////////////////////////////////////////////////////////
    // Factory
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Factory for instantiating [CommandExecutor].
     */
    public interface Factory : Serializable {

        /**
         * Creates a new instance of [CommandExecutor] wrapping [execOperations].
         */
        public fun create(execOperations: ExecOperations): CommandExecutor
    }
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Executes the command built from [args], in an environment configured by registered tools.
 */
@IgnorableReturnValue
public fun CommandExecutor.execute(
    vararg args: Any,
    workingDirectory: File? = null
): ExecResult {
    return execute(Command(*args), workingDirectory)
}

/**
 * Executes the command built from [args], in an environment configured by registered tools and
 * returns the output.
 */
@IgnorableReturnValue
public fun CommandExecutor.executeWithOutput(
    vararg args: Any,
    workingDirectory: File? = null
): String {
    return executeWithOutput(Command(*args), workingDirectory)
}