@file:Suppress("UnstableApiUsage")

package komple.project

import komple.platform.Platform
import komple.project.c.KompleCProjectBase
import komple.project.c.Optimization
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

/**
 * Sealed set of Komple projects.
 */
public sealed class KompleProject(@Internal private val projectName: String) :
    KompleProjectBase,
    Named {

    override fun getName(): String {
        return projectName
    }
}

/**
 * Configures the conventions values.
 */
@Suppress("UnstableApiUsage")
internal fun KompleProject.configureCommonConventions(project: Project) {
    packageName.convention(project.provider { "No package name was provided" })
    sourceFiles.convention(project.provider { error("No source files were provided") })
}

///////////////////////////////////////////////////////////////////////////
// C
///////////////////////////////////////////////////////////////////////////

/**
 * C project.
 */
public abstract class KompleCProject internal constructor(
    projectName: String,
    private val objects: ObjectFactory
) : KompleProject(projectName),
    KompleCProjectBase {

    @get:Input
    internal abstract val platformCompilerOptions: MapProperty<Platform, List<String>>

    @get:Input
    internal abstract val platformLinkerOptions: MapProperty<Platform, List<String>>

    private fun list(configure: Action<in ListProperty<String>>): List<String> {
        val list = objects.listProperty(String::class.java)
        configure.execute(list)
        return list.get()
    }

    override fun compilerOptions(
        platform: Platform,
        configure: Action<in ListProperty<String>>
    ) {
        platformCompilerOptions.put(platform, list(configure))
    }

    /**
     * Returns all the compiler options for the specified [platform].
     */
    public fun compilerOptions(platform: Platform): Provider<List<String>> {
        return compilerOptions.zip(
            platformCompilerOptions.map { it[platform].orEmpty() },
            List<String>::plus
        )
    }

    override fun linkerOptions(
        platform: Platform,
        configure: Action<in ListProperty<String>>
    ) {
        platformLinkerOptions.put(platform, list(configure))
    }

    /**
     * Returns all the linker options for the specified [platform].
     */
    public fun linkerOptions(platform: Platform): Provider<List<String>> {
        return linkerOptions.zip(
            platformLinkerOptions.map { it[platform].orEmpty() },
            List<String>::plus
        )
    }
}

/**
 * Configures the convention values.
 */
internal fun KompleCProject.configureConventions(project: Project) {
    headerFile.convention(project.provider { error("Main header file was not set") })
    headerFilters.convention(headerFile)
    optimization.convention(Optimization.Level2)
}