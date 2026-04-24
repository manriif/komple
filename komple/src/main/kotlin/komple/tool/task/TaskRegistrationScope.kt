package komple.tool.task

import komple.platform.HasHost
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider

/**
 * Base scope for task registration.
 */
public interface TaskRegistrationScope<Extension : KompleToolExtension> :
    HasExtension<Extension>,
    HasHost {

    /**
     * Name of the tool.
     */
    public val toolName: String

    /**
     * Returns the project's [ProviderFactory].
     */
    public val providers: ProviderFactory

    /**
     * Returns a task that always fails when executed.
     */
    public fun unsupported(): TaskProvider<*>

    /**
     * Returns a task that skips the operation.
     */
    public fun skip(): TaskProvider<*>
}