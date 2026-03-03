package komple

import komple.extension.getExtensionByName
import komple.tool.KompleToolConfigurator
import org.gradle.api.Project
import kotlin.reflect.KClass

/**
 * Base for Komple root extension.
 */
public interface KompleRootExtensionBase {

    /**
     * Registers a tool, identified by [name], that is configured by an instance of [klass].
     */
    public fun <Configurator : KompleToolConfigurator> registerTool(
        name: String,
        klass: KClass<Configurator>
    )
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
public inline fun <reified Config : KompleToolConfigurator> KompleRootExtensionBase.registerTool(
    name: String
) {
    registerTool(name, Config::class)
}