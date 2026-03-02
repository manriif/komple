package komple.exec

/**
 * Represents a command ready to execute.
 */
public interface Command {

    /**
     * Returns all the command arguments.
     */
    public val args: Array<out String>

    /**
     * Returns the command as a string.
     */
    public override fun toString(): String
}