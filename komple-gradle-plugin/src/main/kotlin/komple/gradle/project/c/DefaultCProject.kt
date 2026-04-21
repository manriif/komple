package komple.gradle.project.c

import komple.gradle.project.DefaultKompleProject
import komple.platform.Platform
import komple.project.c.COptimization
import komple.project.c.CProject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input

/**
 * Implementation of [CProject].
 */
internal abstract class DefaultCProject internal constructor(
    projectName: String,
    private val objects: ObjectFactory
) : DefaultKompleProject(projectName),
    CProject {

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
    fun compilerOptions(platform: Platform): Provider<List<String>> {
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
    fun linkerOptions(platform: Platform): Provider<List<String>> {
        return linkerOptions.zip(
            platformLinkerOptions.map { it[platform].orEmpty() },
            List<String>::plus
        )
    }
}

/**
 * Configures the convention values.
 */
@Suppress("UnstableApiUsage")
internal fun DefaultCProject.configureConventions(project: Project) {
    headerFile.convention(project.provider { error("Main header file was not set") })
    headerFilters.convention(headerFile)
    optimization.convention(COptimization.Level2)
}