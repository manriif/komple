package komple.gradle

import komple.tool.KompleTool
import komple.tool.KompleToolConfigurator
import org.gradle.api.DomainObjectSet
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.domainObjectSet
import org.gradle.kotlin.dsl.namedDomainObjectSet
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * [KomplePlugin]'s extension.
 */
abstract class KompleExtension @Inject constructor(private val objects: ObjectFactory) :
    KompleBaseExtension {

    /**
     * Registered tools.
     */
    val tools: NamedDomainObjectSet<KompleTool> =
        objects.namedDomainObjectSet(KompleTool::class)

    internal val toolConfigurators =
        objects.polymorphicDomainObjectContainer(KompleToolConfigurator::class.java)

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