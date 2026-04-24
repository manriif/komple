package komple.gradle.tool.graph

/**
 * Exception thrown when a cycle has been found on an attempt to create a dependency.
 */
public class CyclicDependencyException(message: String) : Exception(message)