package komple

import komple.tool.configurator.KompleToolConfigurator
import komple.util.getExtensionByName
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import kotlin.reflect.KClass

/**
 * Base for Komple root extension.
 */
public interface KompleRootExtensionBase {

    /**
     * Registers a tool, identified by [name], that is configured by an instance of [klass].
     */
    @IgnorableReturnValue
    public fun <Configurator : KompleToolConfigurator<*>> registerTool(
        name: String,
        klass: KClass<Configurator>,
        vararg args: Any
    ): NamedDomainObjectProvider<Configurator>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Returns the [KompleRootExtensionBase] for `this` [Project].
 */
public val Project.kompleRootExtension: KompleRootExtensionBase
    get() = rootProject.getExtensionByName(KOMPLE_EXTENSION_NAME)

/**
 * Registers a tool, identified by [name], that is configured by an instance of [Config].
 */
public inline fun <reified Config : KompleToolConfigurator<*>> KompleRootExtensionBase.registerTool(
    name: String,
    vararg args: Any
): NamedDomainObjectProvider<Config> = registerTool(
    name = name,
    klass = Config::class,
    args = args
)