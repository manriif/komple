package komple.gradle.extension

import komple.exec.ExecService
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import javax.inject.Inject

/**
 * Komple extension for subproject.
 * The extension is designed for registered tools and projects consumption.
 */
public abstract class KompleSubProjectExtension @Inject constructor(objects: ObjectFactory) {

    /**
     * Command executor service which can be used to execute command in an environment populated and
     * configured by registered tools.
     */
    public abstract val execService: Property<ExecService>

    /**
     * Available compilations.
     */
    public val compilations: KompleCompilationsExtension =
        extensions.create("compilations", KompleCompilationsExtension::class)

    /**
     * Registered tools.
     */
    public val tools: KompleToolsExtension =
        extensions.create("tools", KompleToolsExtension::class)
}

///////////////////////////////////////////////////////////////////////////
// Configuration
///////////////////////////////////////////////////////////////////////////

/**
 * Configure [this@configureExtension] from the [root].
 */
internal fun KompleSubProjectExtension.configureExtension(root: KompleRootProjectExtension) {
    execService.set(root.execService)

    root.projects.all {
        compilations.extensions.add(
            this.name,
            this
        )
    }

    root.tools.all {
        tools.extensions.add(this.name, this)
    }
}