package komple.gradle.project.c

/**
 * Exception thrown when no tool has registered a task for compiling a project.
 */
internal class MissingCompilerException(message: String, cause: Throwable? = null) :
    Exception(message, cause)