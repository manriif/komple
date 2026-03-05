package komple.tool.configurator

import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.KompleToolExtension
import komple.tool.extension.createExtension

/**
 * Base for implementor of [KompleToolConfigurator] that do not have custom extension.
 */
public abstract class UnextendedKompleToolConfigurator(toolName: String) :
    DefaultKompleToolConfigurator<KompleToolExtension>(toolName) {

    override fun getName(): String {
        return toolName
    }

    override fun ExtensionConfigurationScope<KompleToolExtension>.configureExtension(): KompleToolExtension {
        return createExtension()
    }
}