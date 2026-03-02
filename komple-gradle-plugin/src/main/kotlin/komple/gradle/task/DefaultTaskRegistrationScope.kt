package komple.gradle.task

import komple.gradle.platform.CurrentHost
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.platform.Host
import komple.task.TaskRegistrationScope
import org.gradle.api.Project
import org.gradle.api.provider.ProviderFactory
import kotlin.reflect.KClass

/**
 * Default base implementation for [TaskRegistrationScope].
 */
internal abstract class DefaultTaskRegistrationScope(protected val context: KompleToolConfigContext) :
    TaskRegistrationScope,
    ClosableScope() {

    protected val project: Project
        inline get() = context.komple.project

    override val host: Host
        get() = notClosed { CurrentHost }

    override val providers: ProviderFactory
        get() = notClosed { context.komple.project.providers }

    override val toolName: String
        get() = context.toolName

    /**
     * Returns a conventional name for a task and for the tool.
     */
    protected fun toolTaskName(postfix: String): String {
        return toolTaskName(toolName, postfix)
    }

    override fun <Extension : Any> extension(type: KClass<Extension>): Extension = notClosed {
        return context.komple.retrieve(type)
    }
}