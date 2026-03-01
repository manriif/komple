package komple.integrity

import komple.gradle.HasProject

/**
 * Scope providing factories to [IntegrityCheck].
 *
 * Note that the [project] must not be captured and a [forward] checker should be Gradle
 * configuration-cache compliant if configuration-caching is enabled for the build.
 */
public interface IntegrityCheckScope : HasProject {

    /**
     * Returns an [IntegrityChecker] validating the file integrity against [checksum] using
     * [algorithm].
     */
    public fun checksum(
        checksum: String,
        algorithm: Algorithm
    ): IntegrityChecker

    /**
     * Returns [checker].
     */
    public fun forward(checker: IntegrityChecker): IntegrityChecker
}