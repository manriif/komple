package komple.tool

import komple.extension.ExtensionConfigurationScope
import org.gradle.api.tasks.Internal

/**
 * Base for implementor of [KompleToolConfigurator] with a default implementation for common properties and
 * methods.
 */
public abstract class DefaultKompleToolConfigurator(@Internal protected val toolName: String) :
    KompleToolConfigurator {

    override fun getName(): String? {
        return toolName
    }

    override fun ExtensionConfigurationScope.configureExtension() {}
}