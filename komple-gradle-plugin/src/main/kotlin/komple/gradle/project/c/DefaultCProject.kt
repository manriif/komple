package komple.gradle.project.c

import komple.gradle.project.DefaultKompleProject
import komple.platform.Platform
import komple.project.c.COptimization
import komple.project.c.CProject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import javax.inject.Inject

/**
 * Implementation of [CProject].
 */
internal abstract class DefaultCProject @Inject constructor(projectName: String) :
    DefaultKompleProject(projectName),
    CProject {

    @get:Input
    internal abstract val platformDefinitions: MapProperty<Platform, Map<String, String>>

    @get:Input
    internal abstract val platformCompilerOptions: MapProperty<Platform, List<String>>

    @get:Input
    internal abstract val platformLinkerOptions: MapProperty<Platform, List<String>>

    @get:Input
    internal abstract val platformOptimizations: MapProperty<Platform, COptimization>

    override fun definition(
        platform: Platform,
        configure: Action<MutableMap<String, String>>
    ) {
        val map = mutableMapOf<String, String>()
        configure.execute(map)
        platformDefinitions.put(platform, map.toMap())
    }

    override fun definitions(platform: Platform): Provider<List<String>> {
        val platformDefinitions = platformDefinitions.map { it[platform].orEmpty() }

        return definitions
            .zip(platformDefinitions, Map<String, String>::plus)
            .map { entries -> entries.map { "-D${it.key}=${it.value}" } }
    }

    private fun list(configure: Action<MutableList<String>>): List<String> {
        val list = mutableListOf<String>()
        configure.execute(list)
        return list.toList()
    }

    override fun compilerOptions(
        platform: Platform,
        configure: Action<MutableList<String>>
    ) {
        platformCompilerOptions.put(platform, list(configure))
    }

    /**
     * Returns all the compiler options for the specified [platform].
     */
    override fun compilerOptions(platform: Platform): Provider<List<String>> {
        val platformOptions = platformCompilerOptions.map { it[platform].orEmpty() }
        return compilerOptions.zip(platformOptions, List<String>::plus)
    }

    override fun linkerOptions(
        platform: Platform,
        configure: Action<MutableList<String>>
    ) {
        platformLinkerOptions.put(platform, list(configure))
    }

    /**
     * Returns all the linker options for the specified [platform].
     */
    override fun linkerOptions(platform: Platform): Provider<List<String>> {
        val platformOptions = platformLinkerOptions.map { it[platform].orEmpty() }
        return linkerOptions.zip(platformOptions, List<String>::plus)
    }

    override fun optimization(
        platform: Platform,
        optimization: COptimization
    ) {
        platformOptimizations.put(platform, optimization)
    }

    override fun optimization(platform: Platform): Provider<String> {
        return platformOptimizations
            .zip(optimization) { platforms, default -> platforms[platform] ?: default }
            .map { "-O${it.value}" }
    }
}

///////////////////////////////////////////////////////////////////////////
// Conventions
///////////////////////////////////////////////////////////////////////////

/**
 * Configures the convention values.
 */
@Suppress("UnstableApiUsage")
internal fun DefaultCProject.configureConventions(project: Project) {
    headerFile.convention(project.provider { error("Main header file was not set") })
    headerFilters.convention(headerFile)
}