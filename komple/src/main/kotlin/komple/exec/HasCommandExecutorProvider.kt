package komple.exec

import org.gradle.api.provider.Provider

/**
 * Represents a Komple entity associated with a [CommandExecutor].
 */
public interface HasCommandExecutorProvider {

    /**
     * Provider of the [CommandExecutor] for a specific context.
     */
    public val commandExecutor: Provider<CommandExecutor>
}