package komple.gradle

import komple.extension.getExtensionByName
import komple.tool.KompleTool
import org.gradle.api.Project
import kotlin.reflect.KClass

/**
 * Base for Komple extension.
 */
public interface KompleBaseExtension {

    /**
     * Registers a tool of type [Tool], identified by [name].
     */
    public fun <Tool : KompleTool> registerTool(
        name: String,
        klass: KClass<Tool>
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
 * Registers a tool of type [Tool], identified by [name].
 */
public inline fun <reified Tool : KompleTool> KompleBaseExtension.registerTool(name: String) {
    registerTool(name, Tool::class)
}