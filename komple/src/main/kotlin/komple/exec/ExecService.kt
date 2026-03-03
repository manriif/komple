package komple.exec

import org.gradle.process.ExecResult
import java.io.File

/**
 * Service for command execution in the context of compilation.
 */
public interface ExecService {

    /**
     * Executes the [command] in a configured execution environment where all the registered tools
     * are available in the PATH.
     */
    public fun exec(
        command: Command,
        workingDirectory: File? = null
    ): ExecResult
}