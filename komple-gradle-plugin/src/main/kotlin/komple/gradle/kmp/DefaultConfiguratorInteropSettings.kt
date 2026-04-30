package komple.gradle.kmp

import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject
import org.jetbrains.kotlin.gradle.plugin.CInteropSettings as KmpCInteropSettings

/**
 * Wrapper for [KmpCInteropSettings].
 */
internal abstract class DefaultCInteropSettings @Inject constructor(
    private val settings: KmpCInteropSettings,
    override val generateDefFileTaskProvider: TaskProvider<*>
) : CInteropSettings {

    override fun extraOpts(vararg values: Any) {
        settings.extraOpts(*values)
    }

    override fun extraOpts(values: List<Any>) {
        settings.extraOpts(values)
    }
}