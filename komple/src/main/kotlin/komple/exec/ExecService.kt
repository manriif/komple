package komple.exec

import org.gradle.process.ExecResult
import java.io.File

/**
 * Service for command execution in an environment where registered tools are available.
 */
public interface ExecService {

    /**
     * Executes the [command], using [interpreter], in an environment configured by registered
     * tools.
     */
    public fun exec(
        command: Command,
        interpreter: CommandInterpreter = Bash,
        workingDirectory: File? = null
    ): ExecResult
}