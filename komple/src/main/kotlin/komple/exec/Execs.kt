package komple.exec

import org.gradle.process.ExecOperations
import org.gradle.process.ExecSpec
import java.io.ByteArrayOutputStream
import java.util.logging.Logger

///////////////////////////////////////////////////////////////////////////
// Regular exec
///////////////////////////////////////////////////////////////////////////

/**
 * Sets the full command line, including the executable to be executed plus its arguments.
 */
@IgnorableReturnValue
public fun ExecSpec.commandLine(line: CommandLine): ExecSpec = commandLine(*line.args).also {
    Logger.getGlobal().severe { "Command: ${line.args.joinToString(" ")}" }
}

///////////////////////////////////////////////////////////////////////////
// Exec with output
///////////////////////////////////////////////////////////////////////////

/**
 * Executes a command configured with [action] and returns the output.
 */
public fun ExecOperations.execOutput(action: ExecSpec.() -> Unit): String {
    return ByteArrayOutputStream().use { output ->
        exec {
            standardOutput = output
            action()
        }

        output.toString(Charsets.UTF_8).trimEnd()
    }
}

/**
 * Executes a single line command given [args] and returns the output.
 */
public fun ExecOperations.execOutput(vararg args: Any): String {
    return execOutput {
        commandLine(*args)
    }
}

/**
 * Executes a single line command given [args] and returns the output.
 */
public fun ExecOperations.execOutput(args: Iterable<Any>): String {
    return execOutput {
        commandLine(args)
    }
}

/**
 * Executes [line] and returns the output.
 */
public fun ExecOperations.execOutput(line: CommandLine): String {
    return execOutput {
        commandLine(line = line)
    }
}