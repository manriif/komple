package komple.gradle.kmp

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.listProperty
import org.jetbrains.kotlin.gradle.plugin.CInteropSettings as KmpCInteropSettings

/**
 * Wrapper for [KmpCInteropSettings].
 */
internal class DefaultCInteropSettings(
    private val settings: KmpCInteropSettings,
    private val objects: ObjectFactory
) : CInteropSettings {

    override val sourcesTasks: ListProperty<TaskProvider<*>> = objects.listProperty()

    override fun extraOpts(vararg values: Any) {
        settings.extraOpts(*values)
    }

    override fun extraOpts(values: List<Any>) {
        settings.extraOpts(values)
    }
}