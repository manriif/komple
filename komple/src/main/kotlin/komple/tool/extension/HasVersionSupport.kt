package komple.tool.extension

import org.gradle.api.provider.Property

/**
 * Tools that supports versioning.
 */
public interface HasVersionSupport {

    /**
     * Version of the tool.
     */
    public val version: Property<String>
}