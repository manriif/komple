package komple.gradle.extension

import komple.gradle.tool.KompleToolsExtension

/**
 * Base for Komple extensions.
 */
public interface KompleExtension {

    /**
     * Registered tools.
     */
    public val tools: KompleToolsExtension
}