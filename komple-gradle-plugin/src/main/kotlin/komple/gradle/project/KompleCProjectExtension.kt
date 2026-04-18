package komple.gradle.project

import komple.platform.Platform
import komple.project.c.LibraryType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

/**
 * Extension for a configured C project.
 */
public abstract class KompleCProjectExtension : KompleProjectExtension {

    /**
     * Registers a task that generates a library of type [type] for each platform in [platforms].
     */
    public fun registerLibraryGeneratorTask(
        type: LibraryType,
        platforms: List<Platform>
    ) {

    }
}

/**
 * Registers a task that generates a library of type [type] for each platform in [platforms].
 */
public fun KompleCProjectExtension.registerLibraries(targets: List<KotlinNativeTarget>) {

}