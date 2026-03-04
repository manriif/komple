package komple.gradle.extension

import komple.exec.ExecService
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Komple extension for subproject.
 */
public abstract class KompleExtension @Inject constructor(objects: ObjectFactory) {

    /**
     * Command executor service which can be used to exec command in task.
     */
    public abstract val execService: Property<ExecService>

    /**
     * Available compilations.
     */
    @get:Inject
    public abstract val compilations: KompleCompilationsExtension

    /**
     * Registered tools.
     */
    @get:Inject
    public abstract val tools: KompleToolsExtension
}

///////////////////////////////////////////////////////////////////////////
// Configuration
///////////////////////////////////////////////////////////////////////////

/**
 * Configure [extension] from the [rootExtension].
 */
internal fun configureExtension(
    extension: KompleExtension,
    rootExtension: KompleRootExtension
) {
    extension.execService.set(rootExtension.execService)

    rootExtension.projects.whenObjectAdded {
        extension.compilations.extensions.add(
            name,
            this
        )
    }

    rootExtension.tools.whenObjectAdded {
        extension.tools.extensions.add(name, this)
    }
}