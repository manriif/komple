package komple.tool.configurator

import komple.tool.compile.ExecEnvironmentBuilderScope
import komple.tool.extension.KompleToolExtension
import org.gradle.api.tasks.Internal

/**
 * Base for implementor of [KompleToolConfigurator] with a default implementation for common properties and
 * methods.
 */
public abstract class DefaultKompleToolConfigurator<Extension : KompleToolExtension>(
    @Internal protected val toolName: String
) : KompleToolConfigurator<Extension> {

    override fun getName(): String {
        return toolName
    }

    override fun ExecEnvironmentBuilderScope<Extension>.configureEnvironment() {}
}