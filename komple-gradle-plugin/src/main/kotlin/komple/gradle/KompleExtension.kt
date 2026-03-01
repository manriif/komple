package komple.gradle

import komple.gradle.extension.DefaultExtensiblePolymorphicDomainObjectContainer
import komple.gradle.extension.ExtensionCreator
import komple.gradle.extension.ExtensionCreatorImpl
import komple.tool.KompleTool
import komple.tool.KompleToolConfigurator
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Extension for [KomplePlugin].
 */
abstract class KompleExtension @Inject constructor(private val objects: ObjectFactory) :
    KompleBaseExtension,
    ExtensionCreator by ExtensionCreatorImpl() {

    val tools: PolymorphicDomainObjectContainer<KompleTool>
        field = objects.newInstance<DefaultExtensiblePolymorphicDomainObjectContainer<KompleTool>>(
            objects.polymorphicDomainObjectContainer(KompleTool::class)
        )

    override fun <Tool : KompleTool> declareTool(
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
    }
}