package komple.gradle.task

import komple.gradle.Komple
import komple.gradle.platform.CurrentHost
import komple.gradle.util.ClosableScope
import komple.platform.Host
import komple.task.TaskRegistrationScope
import org.gradle.api.provider.ProviderFactory
import kotlin.reflect.KClass

/**
 * Default base implementation for [TaskRegistrationScope].
 */
internal abstract class DefaultTaskRegistrationScope(
    protected val komple: Komple,
    override val toolName: String
) : TaskRegistrationScope,
    ClosableScope() {

    override val host: Host
        get() = notClosed { CurrentHost }

    override val providers: ProviderFactory
        get() = notClosed { komple.project.providers }

    /**
     * Returns a conventional name for a task and for the tool.
     */
    protected fun toolTaskName(postfix: String): String {
        return toolTaskName(toolName, postfix)
    }

    override fun <Extension : Any> extension(type: KClass<Extension>): Extension = notClosed {
        return komple.retrieve(type)
    }
}