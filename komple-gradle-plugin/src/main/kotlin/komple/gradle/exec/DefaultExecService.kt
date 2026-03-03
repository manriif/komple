package komple.gradle.exec

import komple.exec.Command
import komple.exec.ExecService
import komple.exec.command
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import java.io.File
import javax.inject.Inject

/**
 * Service for command execution in the context of compilation.
 */
internal abstract class DefaultExecService @Inject constructor(
    private val execOperations: ExecOperations
) : BuildService<DefaultExecService.Params>,
    ExecService {

    override fun exec(command: Command, workingDirectory: File?): ExecResult {
        val execEnvironment = parameters.environment.get()

        return execOperations.exec {
            workingDirectory?.let { workingDir = it }

            execEnvironment.variables.get()
                .forEach(::environment)

            execEnvironment.paths.get()
                .takeIf { it.isNotEmpty() }
                ?.fold(System.getenv("PATH")) { left, right ->
                    "$right:$left"
                }
                ?.let { environment("PATH", it) }

            // TODO
            command(command)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    interface Params : BuildServiceParameters {

        val environment: Property<ExecEnvironment>
    }
}