package komple.gradle

import komple.gradle.extension.ExtensionCreator
import komple.gradle.extension.getExtensionByName
import komple.tool.KompleTool
import komple.tool.KompleToolConfigurator
import org.gradle.api.Project
import kotlin.reflect.KClass

/**
 * Base for Komple extensions.
 */
public interface KompleBaseExtension : ExtensionCreator {

    /**
     * Declares a tool of type [klass] and identified by [id].
     * Only one declaration per [klass] is allowed.
     */
    public fun <Tool : KompleTool> declareTool(
        klass: KClass<Tool>,
        id: String,
        configurator: KompleToolConfigurator<Tool>
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