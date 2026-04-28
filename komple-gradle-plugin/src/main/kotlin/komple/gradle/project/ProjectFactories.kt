package komple.gradle.project

import komple.KompleInternalApi
import komple.gradle.project.c.CProjectConfiguratorFactory
import komple.gradle.project.c.DefaultCProject
import komple.gradle.project.c.configureConventions
import komple.project.KompleProject
import komple.project.c.CProject
import org.gradle.api.DomainObjectSet
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance

/**
 * Registers factories for supported [DefaultKompleProject] types.
 */
@KompleInternalApi
internal fun Project.registerProjectFactories(
    projects: ExtensiblePolymorphicDomainObjectContainer<KompleProject>,
    factories: DomainObjectSet<ProjectConfiguratorFactory<*>>
) {
    registerProjectFactory<CProject, DefaultCProject>(
        projects = projects,
        factories = factories,
        configureConventions = DefaultCProject::configureConventions,
        createFactory = ::CProjectConfiguratorFactory,
        objects
    )

    // Force all project creation for subproject DSL
    projects.all {}
}

/**
 * Registers a factory for a [KompleProject] of public type [P] and implementation type [I].
 */
internal inline fun <reified P : KompleProject, reified I : P> Project.registerProjectFactory(
    projects: ExtensiblePolymorphicDomainObjectContainer<KompleProject>,
    factories: DomainObjectSet<ProjectConfiguratorFactory<*>>,
    crossinline configureConventions: (I, Project) -> Unit,
    crossinline createFactory: (I) -> ProjectConfiguratorFactory<*>,
    vararg args: Any
) {
    projects.registerFactory(P::class.java) { name ->
        objects.newInstance<I>(name, *args).apply {
            configureCommonConventions(this@registerProjectFactory)
            configureConventions(this, this@registerProjectFactory)
            factories.add(createFactory(this))
        }
    }
}