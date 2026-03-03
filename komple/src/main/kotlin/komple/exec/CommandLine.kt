package komple.exec

/**
 * Represents a command line, ready to execute.
 */
public interface CommandLine {

    /**
     * Returns all the command arguments.
     */
    public val args: Array<String>

    /**
     * Returns the command as a string.
     */
    public override fun toString(): String
}