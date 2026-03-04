package komple.project

import komple.KompleInternalApi
import komple.compile.KompleCompilation
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer as ExtensibleContainer

///////////////////////////////////////////////////////////////////////////
// Container
///////////////////////////////////////////////////////////////////////////

/**
 * Registers factories for supported [KompleProject] types.
 */
@KompleInternalApi
public fun ExtensibleContainer<KompleProject>.registerFactories(project: Project) {
    registerFactory<KompleCProject>(project, KompleCProject::configureConventions)
}

/**
 * Registers factory for [KompleProject] of type [P].
 */
internal inline fun <reified P : KompleProject> ExtensibleContainer<KompleProject>.registerFactory(
    project: Project,
    crossinline configureConventions: P.(Project) -> Unit
) = registerFactory(P::class.java) { name ->
    project.objects.newInstance<P>(name).apply {
        configureCommonConventions(project)
        configureConventions(project)
    }
}