package komple.tool.extension

import org.gradle.api.provider.Property

/**
 * Tool version can be supplied.
 */
public interface HasVersionSupport {

    /**
     * Version of the tool.
     */
    public val version: Property<String>
}