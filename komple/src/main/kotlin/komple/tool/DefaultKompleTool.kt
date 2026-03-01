package komple.tool

import komple.extension.ExtensionRegistrationScope
import org.gradle.api.tasks.Internal

/**
 * Base for implementor of [KompleTool] with a default implementation for common properties and
 * methods.
 */
public abstract class DefaultKompleTool(@Internal protected val toolName: String) : KompleTool {

    override val displayName: String?
        get() = null

    override fun ExtensionRegistrationScope.configureExtension() {}
}