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
    public fun exec(
        command: Command,
        workingDirectory: File? = null
    ): ExecResult
}