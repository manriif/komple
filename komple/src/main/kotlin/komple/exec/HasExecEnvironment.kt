package komple.exec

/**
 * Owns an instance of [ExecEnvironment].
 */
public interface HasExecEnvironment {

    /**
     * Execution environment.
     */
    public val execEnvironment: ExecEnvironment
}