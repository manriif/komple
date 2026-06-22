package komple.exec

import org.gradle.api.tasks.Nested

/**
 * Owns an instance of [ExecEnvironment].
 */
public interface HasExecEnvironment {

    /**
     * Execution environment.
     */
    @get:Nested
    public val execEnvironment: ExecEnvironment
}