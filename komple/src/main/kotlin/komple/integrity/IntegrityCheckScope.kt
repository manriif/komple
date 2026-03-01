package komple.integrity

import komple.gradle.HasProject

/**
 * Scope providing factories to [IntegrityCheck].
 *
 * Note that the [project] must not be captured and a [custom] checker must not violate Gradle
 * configuration-cache requirements.
 */
public interface IntegrityCheckScope : HasProject {

    /**
     * Validates the file integrity against [checksum] using [algorithm].
     */
    public fun checksum(checksum: String, algorithm: Algorithm): IntegrityCheck

    /**
     * Validates the file integrity using custom [checker].
     */
    public fun custom(checker: IntegrityChecker): IntegrityCheck
}