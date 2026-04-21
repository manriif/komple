package komple.gradle.project.c

import komple.gradle.project.ProjectConfiguratorFactory
import komple.project.ProjectConfigurator
import kotlin.reflect.KClass

/**
 * Implementation of [ProjectConfiguratorFactory] for [DefaultCProject].
 */
internal class CProjectConfiguratorFactory(override val kProject: DefaultCProject) :
    ProjectConfiguratorFactory<CProjectExtension> {

    override val extensionType: KClass<out CProjectExtension>
        get() = CProjectExtension::class

    override fun createConfigurator(extension: CProjectExtension): ProjectConfigurator {
        return DefaultCProjectConfigurator(extension)
    }
}