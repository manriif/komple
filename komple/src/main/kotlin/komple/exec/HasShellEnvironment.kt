package komple.exec

/**
 * Owns an instance of [ShellEnvironment].
 */
public interface HasShellEnvironment {

    /**
     * Shell environment.
     */
    public val shellEnvironment: ShellEnvironment
}