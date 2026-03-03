package komple.tool

import komple.extension.ExtensionConfigurationScope
import komple.tool.compile.ExecEnvironmentBuilderScope
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

    override fun ExtensionConfigurationScope.configureExtension(): Unit = Unit

    override fun ExecEnvironmentBuilderScope.configureEnvironment(): Unit = Unit
}