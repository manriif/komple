package komple.platform

/**
 * Represents a Komple entity associated with a [Host].
 */
public interface HasHost {

    /**
     * The [Host] associated with the entity.
     */
    public val host: Host
}
