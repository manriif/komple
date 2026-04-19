package komple.project

import komple.KompleInternalApi
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer as ExtensibleContainer

/**
 * Registers factories for supported [KompleProject] types.
 */
@KompleInternalApi
public fun ExtensibleContainer<KompleProject>.registerFactories(project: Project) {
    registerFactory<KompleCProject>(project, KompleCProject::configureConventions, project.objects)
}

/**
 * Registers factory for [KompleProject] of type [P].
 */
internal inline fun <reified P : KompleProject> ExtensibleContainer<KompleProject>.registerFactory(
    project: Project,
    crossinline configureConventions: P.(Project) -> Unit,
    vararg args: Any
) = registerFactory(P::class.java) { name ->
    project.objects.newInstance<P>(name, *args).apply {
        configureCommonConventions(project)
        configureConventions(project)
    }
}