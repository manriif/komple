package komple.gradle.extension

import komple.KompleRootExtensionBase
import komple.exec.ExecService
import komple.project.KompleProject
import komple.tool.KompleTool
import komple.tool.KompleToolConfigurator
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.domainObjectSet
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Komple extension for the root project.
 */
public abstract class KompleRootExtension @Inject constructor(
    internal val execService: Provider<ExecService>,
    private val objects: ObjectFactory
) : KompleRootExtensionBase {

    /**
     * Registered tools' configurators.
     */
    internal val toolConfigurators =
        objects.polymorphicDomainObjectContainer(KompleToolConfigurator::class)

    /**
     * Projects exposed as polymorphic container.
     */
    internal val extensibleProjects =
        objects.polymorphicDomainObjectContainer(KompleProject::class)

    /**
     * Komple projects.
     */
    public val projects: NamedDomainObjectSet<KompleProject>
        get() = extensibleProjects

    /**
     * Configured tools.
     */
    internal val tools = objects.domainObjectSet(KompleTool::class)

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