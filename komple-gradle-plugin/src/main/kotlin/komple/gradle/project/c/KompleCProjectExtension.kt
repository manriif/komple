package komple.gradle.project.c

import komple.gradle.kmp.CInteropSettings
import komple.gradle.kmp.DefFileOptions
import komple.gradle.kmp.DefaultCInteropSettings
import komple.gradle.kmp.registerGenerateCInteropDefTask
import komple.gradle.kmp.toPlatform
import komple.gradle.project.KompleProjectExtension
import komple.gradle.project.projectGeneratedOutputDir
import komple.platform.Platform
import komple.project.KompleCProject
import komple.project.c.CLibraryType
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

/**
 * Extension for a configured C project.
 */
public abstract class KompleCProjectExtension(
    private val project: Project,
    private val cProject: KompleCProject
) : KompleProjectExtension {

    /**
     * Registers a task that generates a library of type [type] for the provided [platform].
     */
    public fun createLibrary(
        type: CLibraryType,
        platform: Platform
    ): CLibrary {

    }

    /**
     * Registers a task that generates a library of type [type] for the provided [target].
     * Note that the `LINUX_ARM32_HFP` target is not supported.
     */
    public fun createLibrary(
        type: CLibraryType,
        target: KotlinNativeTarget,
        configureInterop: (CInteropSettings.() -> Unit)? = null
    ): CLibrary {
        val platform = target.konanTarget.toPlatform()
        val library = createLibrary(type, platform)

        target.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME) {
            compileTaskProvider.configure {
                dependsOn(library.compileTaskProvider)
            }

            cinterops.register(cProject.name) {
                val settings = DefaultCInteropSettings(this, project.objects)
                configureInterop?.invoke(settings)

                val cInteropDir = project.projectGeneratedOutputDir(cProject.name, "cinterop")
                val defFileName = "${cProject.name}_${platform.altName}.def"
                val defFileProvider = cInteropDir.map { it.file(defFileName) }

                val defFile = project.objects.fileProperty().apply {
                    set(defFileProvider)
                }

                val defFileOptions = DefFileOptions(
                    excludedFunctions = settings.excludedFunctions,
                    noStringConversion = settings.noStringConversion,
                    outputFile = defFile,
                    libraryFile = library.outputFile
                )

                val generateCInteropDefTaskProvider = project.registerGenerateCInteropDefTask(
                    cProject = cProject,
                    options = defFileOptions,
                    platform = platform,
                )

                settings.sourcesTasks.get().takeUnless(List<*>::isEmpty)?.let { providers ->
                    generateCInteropDefTaskProvider.configure {
                        providers.forEach { provider ->
                            dependsOn(provider)
                        }
                    }
                }

                project.tasks.named(interopProcessingTaskName).configure {
                    dependsOn(library.compileTaskProvider)
                    dependsOn(generateCInteropDefTaskProvider)
                }

                definitionFile = defFile.asFile.get()
                includeDirs(cProject.includeDirs)
            }
        }

        return library
    }
}