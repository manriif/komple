package komple.task

import komple.extension.HasExtension
import komple.platform.HasHost
import org.gradle.api.provider.ProviderFactory

/**
 * Base scope for task registration.
 */
public interface TaskRegistrationScope : HasHost, HasExtension {

    /**
     * Name of the tool.
     */
    public val toolName: String

    /**
     * Access the project's [ProviderFactory].
     */
    public val providers: ProviderFactory
}