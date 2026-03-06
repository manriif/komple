package komple.gradle.extension

import komple.KompleRootExtension
import komple.exec.ExecService
import komple.project.KompleProject
import komple.tool.KompleTool
import komple.tool.configurator.KompleToolConfigurator
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.domainObjectSet
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Komple extension for the root project.
 * The extension is designed for tools and projects declaration.
 */
public abstract class KompleRootProjectExtension @Inject constructor(
    internal val execService: Provider<ExecService>,
    private val objects: ObjectFactory
) : KompleRootExtension {

    private val registeredToolClasses = mutableSetOf<KClass<*>>()

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

    override fun <Configurator : KompleToolConfigurator<*>> registerTool(
        name: String,
        klass: KClass<Configurator>,
        vararg args: Any
    ): NamedDomainObjectProvider<Configurator> {
        check(registeredToolClasses.add(klass)) {
            "A tool was already registered for configurator class $klass"
        }

        val javaClass = klass.java

        toolConfigurators.registerFactory(javaClass) { name ->
            objects.newInstance(javaClass, name, *args)
        }

        return toolConfigurators.register(name, javaClass)
    }
}