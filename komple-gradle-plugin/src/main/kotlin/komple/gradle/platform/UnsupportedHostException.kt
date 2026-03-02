package komple.gradle.platform

import org.gradle.api.GradleException

/**
 * Exception thrown when an operation is not available on host.
 */
internal class UnsupportedHostException(message: String) : GradleException(message)