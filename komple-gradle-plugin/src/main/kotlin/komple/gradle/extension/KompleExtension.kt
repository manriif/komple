package komple.gradle.extension

import komple.compile.KompleCompilation
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.namedDomainObjectSet
import javax.inject.Inject

/**
 * Komple extension for subproject.
 */
public abstract class KompleExtension @Inject constructor(objects: ObjectFactory) {

    /**
     * Registered projects.
     */
    @get:Inject
    public abstract val projects: KompleProjectsExtension

    /**
     * Registered tools.
     */
    @get:Inject
    public abstract val tools: KompleToolsExtension

    /**
     * Compilations.
     */
    public val compilations: NamedDomainObjectSet<KompleCompilation> =
        objects.namedDomainObjectSet(KompleCompilation::class)
}