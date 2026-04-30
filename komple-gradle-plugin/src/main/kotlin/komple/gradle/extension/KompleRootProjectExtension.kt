package komple.gradle.extension

import komple.KompleRootExtension
import komple.exec.Bash
import komple.exec.CommandInterpreter
import komple.exec.ExtendableExecEnvironment
import komple.gradle.exec.DefaultExecEnvironment
import komple.gradle.project.ProjectConfiguratorFactory
import komple.gradle.tool.DefaultKompleTool
import komple.gradle.tool.KompleToolsExtension
import komple.project.KompleProject
import komple.tool.configurator.KompleToolConfigurator
import org.gradle.api.DomainObjectSet
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Komple extension for the root project.
 * The extension is designed for tools and projects declaration.
 */
public abstract class KompleRootProjectExtension @Inject constructor(
    private val objects: ObjectFactory
) : KompleExtension,
    KompleRootExtension {

    private val registeredToolClasses = mutableSetOf<KClass<*>>()

    /**
     * The interpreter to use for command execution.
     * Default to [Bash].
     */
    public abstract val commandInterpreter: Property<CommandInterpreter>

    /**
     * Exec environments exposed as [DefaultExecEnvironment].
     */
    internal abstract val defaultExecEnvironments: NamedDomainObjectContainer<DefaultExecEnvironment>

    /**
     * Execution environments.
     */
    public val execEnvironments: NamedDomainObjectContainer<out ExtendableExecEnvironment>
        get() = defaultExecEnvironments

    /**
     * Projects exposed as polymorphic container.
     */
    internal abstract val extensibleProjects: ExtensiblePolymorphicDomainObjectContainer<KompleProject>

    /**
     * Registered project configurators.
     */
    internal abstract val projectConfiguratorFactories: DomainObjectSet<ProjectConfiguratorFactory<*>>

    /**
     * Komple projects.
     */
    public val projects: PolymorphicDomainObjectContainer<KompleProject>
        get() = extensibleProjects

    /**
     * Registered tool configurators.
     */
    internal abstract val toolConfigurators: ExtensiblePolymorphicDomainObjectContainer<KompleToolConfigurator<*>>

    /**
     * Configured tools.
     */
    internal abstract val configuredTools: DomainObjectSet<DefaultKompleTool<*>>

    override val tools: KompleToolsExtension =
        extensions.create("tools", KompleToolsExtension::class)

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

    extensions.run {
        add<NamedDomainObjectContainer<out ExtendableExecEnvironment>>(
            name = ::execEnvironments.name,
            extension = execEnvironments
        )

        add<PolymorphicDomainObjectContainer<KompleProject>>(
            name = ::projects.name,
            extension = projects
        )
    }

    defaultExecEnvironments.configureEach {
        commandInterpreter = this@configureConventions.commandInterpreter
    }
}