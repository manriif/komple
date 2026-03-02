package komple.platform

import java.io.Serializable

/**
 * Host for which project is applied.
 */
public interface Host : Serializable {

    /**
     * Operating system the host is running.
     */
    public val operatingSystem: OperatingSystem.Host

    /**
     * CPU architecture of the host.
     */
    public val architecture: Architecture.Host

    /**
     * Returns the [operatingSystem] when destructuring.
     */
    public operator fun component1(): OperatingSystem.Host {
        return operatingSystem
    }

    /**
     * Returns the [architecture] when destructuring.
     */
    public operator fun component2(): Architecture.Host {
        return architecture
    }
}