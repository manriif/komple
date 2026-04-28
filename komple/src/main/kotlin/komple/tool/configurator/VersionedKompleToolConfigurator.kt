package komple.tool.configurator

import komple.tool.extension.ExtensionConfigurationScope
import komple.tool.extension.HasChecksumSupport
import komple.tool.extension.HasVersionSupport
import komple.tool.extension.KompleToolExtension
import komple.tool.extension.createExtension
import org.gradle.api.Project

/**
 * Base for implementor of [KompleToolConfigurator] that do not need a custom extension and accept
 * a version.
 */
public abstract class VersionedKompleToolConfigurator(toolName: String) :
    DefaultKompleToolConfigurator<VersionedKompleToolConfigurator.Extension>(toolName) {

    override fun getName(): String {
        return toolName
    }

    /**
     * Configures the [Extension] with defaults values.
     */
    protected abstract fun Extension.configure(project: Project)

    final override fun ExtensionConfigurationScope<Extension>.configureExtension(): Extension {
        return createExtension {
            extension.configure(project)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extension
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Extension for tools that do not need a custom extension and accept a version.
     */
    public interface Extension :
        KompleToolExtension,
        HasVersionSupport,
        HasChecksumSupport
}