package komple.tool.jext

import komple.tool.jext.generator.JextractBindingGenerator
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import javax.inject.Inject

/**
 * Compilation extension for Jextract.
 */
public abstract class JextractCompilationExtension @Inject constructor(objects: ObjectFactory) {

    internal val extensibleGenerateBindingsTasks: ExtensiblePolymorphicDomainObjectContainer<JextractBindingGenerator> =
        objects.polymorphicDomainObjectContainer(JextractBindingGenerator::class)

    /**
     * Bindings generator tasks.
     */
    public val generateBindingsTasks: NamedDomainObjectContainer<JextractBindingGenerator>
        get() = extensibleGenerateBindingsTasks
}