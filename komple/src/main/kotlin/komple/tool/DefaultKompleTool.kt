package komple.tool

import org.gradle.api.tasks.Internal

/**
 * Base for implementor of [KompleTool] with a default implementation for common properties and
 * methods.
 */
public abstract class DefaultKompleTool(@Internal protected val toolName: String) : KompleTool {

    final override fun getName(): String {
        return toolName
    }
}