package komple.gradle.tasks

import komple.gradle.KompleExtension
import komple.gradle.kompleExtension
import komple.gradle.platform.CurrentHost
import komple.gradle.scope.ClosableScope
import komple.platform.Host
import komple.tasks.TaskRegistrationScope
import org.gradle.api.Project
import java.util.Locale.getDefault
import kotlin.reflect.KClass

/**
 * Default base implementation for [TaskRegistrationScope].
 */
internal abstract class DefaultTaskRegistrationScope(
    protected val project: Project,
    protected val toolName: String
) : TaskRegistrationScope,
    ClosableScope() {

    override val host: Host
        get() = CurrentHost

    /**
     * Returns a conventional name for a task and for the tool.
     */
    protected fun taskName(postfix: String): String {
        val prefix = toolName.replaceFirstChar { it.lowercase(getDefault()) }
        return "$prefix$postfix"
    }

    override fun <Extension : Any> retrieveExtension(type: KClass<Extension>): Extension {
        val kompleExtension = project.kompleExtension as KompleExtension


    }
}