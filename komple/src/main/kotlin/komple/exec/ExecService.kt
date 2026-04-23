package komple.exec

import org.gradle.process.ExecResult
import java.io.File

/**
 * Service for command execution in an environment where registered tools are available.
 */
public interface ExecService {

    /**
     * Executes the [command], in an environment configured by registered tools.
     */
    @IgnorableReturnValue
    public fun execute(
        command: Command,
        workingDirectory: File? = null
    ): ExecResult
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Executes the command built from [args], in an environment configured by registered tools.
 */
@IgnorableReturnValue
public fun ExecService.execute(
    vararg args: Any,
    workingDirectory: File? = null
): ExecResult {
    return execute(Command(*args), workingDirectory)
}