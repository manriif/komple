package komple.gradle.kmp

import komple.gradle.project.c.DefaultCProject
import komple.gradle.project.projectTaskName
import komple.gradle.project.registerProjectTask
import komple.gradle.util.pascalCased
import komple.platform.Platform
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Registers and returns the task responsible for generating the cinterop definition file for
 * [platform].
 */
internal fun Project.registerGenerateCInteropDefTask(
    cProject: DefaultCProject,
    optionsProvider: Provider<DefFileOptions>,
    platform: Platform,
): TaskProvider<*> = tasks.registerProjectTask(
    name = projectTaskName(cProject.name, "generateCInteropDef${platform.altName.pascalCased()}"),
    type = DefaultTask::class,
    cacheable = true
) {
    outputs.file(optionsProvider.flatMap { it.outputFile })

    inputs.run {
        file(cProject.headerFile)
        file(optionsProvider.flatMap { it.libraryFile })
        property("excludedFunctions", optionsProvider.flatMap { it.excludedFunctions })
        property("noStringConversion", optionsProvider.flatMap { it.noStringConversion })
        property("platform", platform)
    }

    doLast {
        val options = optionsProvider.get()

        options.outputFile.get().asFile
            .apply { parentFile.mkdirs() }
            .writeText(cProject.createDefContent(options, platform))
    }
}

/**
 * Returns the definition file content.
 */
private fun DefaultCProject.createDefContent(
    options: DefFileOptions,
    platform: Platform,
): String {
    val libraryFile = options.libraryFile.get().asFile
    val headerFileName = headerFile.get().asFile.name

    var content = """
        |language = C
        |package = ${packageName.get()}
        |headers = $headerFileName
        |staticLibraries = ${libraryFile.name}
        |libraryPaths = ${libraryFile.parentFile.absolutePath}
    """.trimMargin()

    headerFilters.takeUnless { it.isEmpty }?.let { files ->
        content += "\nheaderFilter = ${files.joinToString(" ") { it.name }}"
    }

    compilerOptions(platform).get().takeUnless { it.isEmpty() }?.let { options ->
        content += "\ncompilerOpts = ${options.joinToString(" ")}"
    }

    linkerOptions(platform).get().takeUnless { it.isEmpty() }?.let { options ->
        content += "\nlinkerOpts = ${options.joinToString(" ")}"
    }

    options.excludedFunctions.get().takeUnless { it.isEmpty() }?.let { functions ->
        content += "\nexcludedFunctions = ${functions.joinToString(" ")}"
    }

    options.noStringConversion.get().takeUnless { it.isEmpty() }?.let { functions ->
        content += "\nnoStringConversion = ${functions.joinToString(" ")}"
    }

    return content
}