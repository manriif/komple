package komple.gradle.project

import komple.gradle.tool.DefaultKompleTool
import komple.project.ProjectConfigurator
import kotlin.reflect.KClass

/**
 * Factory for [ProjectConfigurator].
 */
internal interface ProjectConfiguratorFactory<Extension : KompleProjectExtension> {

    /**
     * Target project.
     */
    val kProject: DefaultKompleProject

    /**
     * [KClass] of [Extension].
     */
    val extensionType: KClass<out Extension>

    /**
     * Returns a new [ProjectConfigurator].
     */
    fun createConfigurator(
        extension: Extension,
        tool: DefaultKompleTool<*>,
    ): ProjectConfigurator
}