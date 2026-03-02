package komple.gradle

import komple.extension.getExtensionByName
import komple.tool.KompleToolConfigurator
import org.gradle.api.Project
import kotlin.reflect.KClass

/**
 * Base for Komple extension.
 */
public interface KompleBaseExtension {

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
 * Returns the [KompleBaseExtension] for `this` [Project].
 */
public val Project.kompleExtension: KompleBaseExtension
    get() = getExtensionByName(KOMPLE_EXTENSION_NAME)

/**
 * Registers a tool, identified by [name], that is configured by an instance of [Configurator].
 */
public inline fun <reified Configurator : KompleToolConfigurator> KompleBaseExtension.registerTool(
    name: String
) {
    registerTool(name, Configurator::class)
}