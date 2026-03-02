package komple.gradle

import komple.gradle.extension.KompleToolsConfigsExtension
import komple.gradle.extension.KompleToolsExtension
import komple.gradle.extension.extensions
import komple.tool.KompleToolConfigurator
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * [KomplePlugin]'s extension.
 */
abstract class KompleExtension @Inject constructor(private val objects: ObjectFactory) :
    KompleBaseExtension {

    /**
     * Configured tools.
     */
    @get:Inject
    abstract val tools: KompleToolsExtension

    /**
     * Registered tools configuration DSLs.
     */
    @get:Inject
    abstract val toolsConfigs: KompleToolsConfigsExtension

    /**
     * Registered tools' configurators.
     */
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

///////////////////////////////////////////////////////////////////////////
// Extension
///////////////////////////////////////////////////////////////////////////

/**
 * Registers the nested extensions for `this` [KompleExtension].
 */
internal fun KompleExtension.registerNestedExtensions() {
    /*extensions.run {
        add<KompleToolsExtension>(
            name = KompleExtension::tools.name,
            extension = tools
        )

        add<KompleToolsConfigsExtension>(
            name = KompleExtension::toolsConfigs.name,
            extension = toolsConfigs
        )
    }*/
}