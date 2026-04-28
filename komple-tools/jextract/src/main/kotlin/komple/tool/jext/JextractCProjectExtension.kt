package komple.tool.jext

import komple.tool.jext.generator.JextractBindingGenerator
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.NamedDomainObjectContainer
import javax.inject.Inject

/**
 *
 * Jextract extension for C Project.
 */
public abstract class JextractCProjectExtension @Inject internal constructor() {

    internal abstract val extensibleBindingGenerators: ExtensiblePolymorphicDomainObjectContainer<JextractBindingGenerator>

    /**
     * Bindings generators.
     */
    public val bindingGenerators: NamedDomainObjectContainer<JextractBindingGenerator>
        get() = extensibleBindingGenerators
}