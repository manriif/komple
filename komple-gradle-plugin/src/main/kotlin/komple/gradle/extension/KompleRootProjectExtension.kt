package komple.gradle.extension

import komple.KompleRootExtension
import komple.exec.Bash
import komple.exec.CommandInterpreter
import komple.exec.ExecService
import komple.gradle.tool.DefaultKompleTool
import komple.project.KompleProject
import komple.tool.configurator.KompleToolConfigurator
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.domainObjectSet
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Komple extension for the root project.
 * The extension is designed for tools and projects declaration.
 */
public abstract class KompleRootProjectExtension @Inject constructor(
    private val objects: ObjectFactory
) : KompleRootExtension {

    private val registeredToolClasses = mutableSetOf<KClass<*>>()
    internal abstract val execService: Property<ExecService>

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
     * The interpreter to use for command execution.
     * Default to [Bash].
     */
    public abstract val commandInterpreter: Property<CommandInterpreter>

    /**
     * Komple projects.
     */
    public val projects: PolymorphicDomainObjectContainer<KompleProject>
        get() = extensibleProjects

    /**
     * Configured tools.
     */
    internal val tools = objects.domainObjectSet(DefaultKompleTool::class)

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

///////////////////////////////////////////////////////////////////////////
// Conventions
///////////////////////////////////////////////////////////////////////////

/**
 * Configures conventions values for [KompleRootProjectExtension].
 */
internal fun KompleRootProjectExtension.configureConventions() {
    commandInterpreter.convention(Bash)
}