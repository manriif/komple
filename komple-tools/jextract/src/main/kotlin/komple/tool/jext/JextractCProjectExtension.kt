package komple.tool.jext

import komple.tool.jext.generator.JextractBindingGenerator
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import javax.inject.Inject

/**
 *
 * Jextract extension for C Project.
 */
public abstract class JextractCProjectExtension @Inject constructor(objects: ObjectFactory) {

    internal val extensibleBindingGenerators: ExtensiblePolymorphicDomainObjectContainer<JextractBindingGenerator> =
        objects.polymorphicDomainObjectContainer(JextractBindingGenerator::class)

    /**
     * Bindings generators.
     */
    public val bindingGenerators: NamedDomainObjectContainer<JextractBindingGenerator>
        get() = extensibleBindingGenerators
}