package komple.gradle.exec

import komple.exec.ExecEnvironment

/**
 * Provides an [ExecEnvironment].
 */
internal fun interface ExecEnvironmentProvider {

    /**
     * Returns the [ExecEnvironment].
     */
    fun get(): ExecEnvironment
}