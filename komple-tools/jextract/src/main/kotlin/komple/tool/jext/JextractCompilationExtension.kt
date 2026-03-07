package komple.tool.jext

import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import javax.inject.Inject

/**
 * Compilation extension for Jextract.
 */
public abstract class JextractCompilationExtension @Inject constructor(objects: ObjectFactory) {

    internal val extensibleGenerateBindingsTasks: ExtensiblePolymorphicDomainObjectContainer<JextractGenerateBindingsTask> =
        objects.polymorphicDomainObjectContainer(JextractGenerateBindingsTask::class)

    /**
     * Bindings generator tasks.
     */
    public val generateBindingsTasks: NamedDomainObjectContainer<JextractGenerateBindingsTask>
        get() = extensibleGenerateBindingsTasks
}