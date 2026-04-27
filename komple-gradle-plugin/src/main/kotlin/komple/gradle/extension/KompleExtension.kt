package komple.gradle.extension

import komple.gradle.exec.DefaultCommandExecutor
import komple.gradle.tool.KompleToolsExtension
import org.gradle.api.NamedDomainObjectContainer

/**
 * Base for Komple extensions.
 */
public interface KompleExtension {

    /**
     * Command executors.
     */
    public val commandExecutors: NamedDomainObjectContainer<DefaultCommandExecutor>

    /**
     * Registered tools.
     */
    public val tools: KompleToolsExtension
}