package komple.tool.install

import komple.extension.HasExtension
import komple.platform.HasHost
import org.gradle.api.provider.ProviderFactory

/**
 * Base scope for task registration.
 */
public interface TaskRegistrationScope :
    HasExtension,
    HasHost {

    /**
     * Name of the tool.
     */
    public val toolName: String

    /**
     * Returns the project's [ProviderFactory].
     */
    public val providers: ProviderFactory
}