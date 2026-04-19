package komple.gradle.kmp

import komple.gradle.project.projectTaskName
import komple.gradle.project.registerProjectTask
import komple.gradle.util.pascalCased
import komple.platform.Platform
import komple.project.KompleCProject
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.tasks.DefFileTask

/**
 * Registers and returns the task responsible for generating the cinterop definition file for
 * [platform].
 */
internal fun Project.registerGenerateCInteropDefTask(
    cProject: KompleCProject,
    options: DefFileOptions,
    platform: Platform,
): TaskProvider<*> = registerProjectTask(
    name = projectTaskName(cProject.name, "generateCInteropDef${platform.altName.pascalCased()}"),
    type = DefFileTask::class
) {
    outputs.file(options.outputFile)

    inputs.run {
        file(cProject.headerFile)
        file(options.libraryFile)
        property("excludedFunctions", options.excludedFunctions)
        property("noStringConversion", options.noStringConversion)
        property("platform", platform)
    }

    doLast {
        options.outputFile.get().asFile
            .apply { parentFile.mkdirs() }
            .writeText(cProject.createDefContent(options, platform))
    }
}

/**
 * Returns the definition file content.
 */
private fun KompleCProject.createDefContent(
    options: DefFileOptions,
    platform: Platform,
): String {
    val definitions = defines.map { entries ->
        entries.map { "-D${it.key}=${it.value}" }
    }

    val compilerOptions = compilerOptions(platform)
        .zip(definitions, List<String>::plus)
        .get()

    val linkerOptions = linkerOptions(platform).get()
    val libraryFile = options.libraryFile.get().asFile
    val headerFileName = headerFile.get().asFile.name
    val filterHeaderFileNames = headerFilters.joinToString(" ") { it.name }
    val excludedFunctions = options.excludedFunctions.get().joinToString(" ")
    val noStringConversion = options.noStringConversion.get().joinToString(" ")

    return """
        |language = C
        |package = $packageName
        |headers = $headerFileName
        |headerFilter = $headerFileName $filterHeaderFileNames
        |compilerOpts = $compilerOptions
        |linkerOpts = $linkerOptions
        |staticLibraries = ${libraryFile.name}
        |libraryPaths = ${libraryFile.parentFile.absolutePath}
        |noStringConversion = $noStringConversion
        |excludedFunctions = $excludedFunctions
    """.trimMargin()
}