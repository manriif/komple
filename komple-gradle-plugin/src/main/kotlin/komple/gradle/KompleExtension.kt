package komple.gradle

import komple.extension.ExtensionRegistrationScope
import komple.gradle.extension.DefaultExtensionRegistrationScope
import komple.tool.KompleTool
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Extension for [KomplePlugin].
 */
abstract class KompleExtension @Inject constructor(private val objects: ObjectFactory) :
    KompleBaseExtension,
    ExtensionRegistrationScope by DefaultExtensionRegistrationScope() {

        internal val

    internal val tools = objects.namedDomainObjectSet(KompleTool::class.java)

    override fun <Tool : KompleTool> registerTool(name: String, klass: KClass<Tool>) {
        tools
    }

    /*override fun <Tool : KompleTool> declareTool(
        klass: KClass<Tool>,
        id: String,
        configurator: KompleToolConfigurator<Tool>
    ) {
        tools.registerDefaultName(klass.java, id)

        tools.registerFactory(klass.java) { name ->
            objects.newInstance(javaClass, name).apply {
                configurator?.configure(this, project)

                project.configureAndRegisterPluginConfig(
                    extension = extension,
                    plugin = this,
                    pluginId = id,
                )
            }
        }
    }*/
}