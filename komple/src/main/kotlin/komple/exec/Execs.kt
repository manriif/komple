package komple.exec

import org.gradle.process.ExecOperations
import org.gradle.process.ExecSpec
import java.io.ByteArrayOutputStream

///////////////////////////////////////////////////////////////////////////
// Regular exec
///////////////////////////////////////////////////////////////////////////

/**
 * Sets the full command line, including the executable to be executed plus its arguments.
 */
public fun ExecSpec.command(command: Command) {
    commandLine(*command.args)
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
 * Executes [command] and returns the output.
 */
public fun ExecOperations.execOutput(command: Command): String {
    return execOutput {
        command(command)
    }
}