package komple.gradle.extension

import komple.KompleRootExtensionBase
import komple.project.KompleProject
import komple.tool.KompleTool
import komple.tool.KompleToolConfigurator
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.domainObjectSet
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Komple extension for the root project.
 */
public abstract class KompleRootExtension @Inject constructor(private val objects: ObjectFactory) :
    KompleRootExtensionBase {

    /**
     * Komple projects.
     */
    public val projects: ExtensiblePolymorphicDomainObjectContainer<KompleProject> =
        objects.polymorphicDomainObjectContainer(KompleProject::class)

    /**
     * Configured tools.
     */
    internal val tools = objects.domainObjectSet(KompleTool::class)

    /**
     * Registered tools' configurators.
     */
    internal val toolConfigurators =
        objects.polymorphicDomainObjectContainer(KompleToolConfigurator::class)

    override fun <Configurator : KompleToolConfigurator> registerTool(
        name: String,
        klass: KClass<Configurator>
    ) {
        toolConfigurators.registerFactory(klass.java) { name ->
            objects.newInstance(klass, name)
        }

        toolConfigurators.register(name, klass.java)
    }
}