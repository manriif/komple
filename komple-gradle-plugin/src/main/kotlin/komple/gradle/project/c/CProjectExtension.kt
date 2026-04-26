package komple.gradle.project.c

import komple.gradle.kmp.CInteropSettings
import komple.gradle.kmp.DefFileOptions
import komple.gradle.kmp.DefaultCInteropSettings
import komple.gradle.kmp.registerGenerateCInteropDefTask
import komple.gradle.kmp.toPlatform
import komple.gradle.project.KompleProjectExtension
import komple.gradle.project.projectGeneratedOutputDir
import komple.gradle.project.projectTaskName
import komple.gradle.project.registerProjectTask
import komple.gradle.util.pascalCased
import komple.platform.Platform
import komple.project.c.CCompilation
import komple.project.c.CCompileTask
import komple.project.c.CLibraryType
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.newInstance
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import javax.inject.Inject

/**
 * Extension for a configured C project.
 */
public abstract class CProjectExtension @Inject internal constructor(
    internal val cProject: DefaultCProject,
    internal val tasks: TaskContainer,
    internal val layout: ProjectLayout
) : KompleProjectExtension {

    internal val compileTaskFactories = mutableListOf<CCompileTaskFactory<*>>()

    /**
     * Registers a [Task] obtained from [factory] for [compilation] and returns a [TaskProvider] to
     * it.
     */
    private fun <Task : CCompileTask<*, *>> createCompileTaskProvider(
        factory: CCompileTaskFactory<Task>,
        compilation: CCompilation,
    ): TaskProvider<out Task> {
        val taskName = projectTaskName(
            projectName = cProject.name,
            postfix = "generate${compilation.libraryType.name}" +
                    "Library${compilation.platform.altName.pascalCased()}"
        )

        return tasks.registerProjectTask(taskName, factory.klass) {
            this.cProject = cProject
            this.compilations.add(compilation)
            this.commandExecutor = factory.commandExecutor

            factory.configure?.invoke(this)
        }
    }

    /**
     * Registers a task that generate a library of type [type] for the provided [platform].
     */
    public fun createLibrary(
        type: CLibraryType,
        platform: Platform
    ): CLibrary {
        val factory = compileTaskFactories.firstOrNull { it.platformFilter(platform) }
            ?: throw MissingCompilerException(
                "No compile task could be created for compiling on platform $platform, " +
                        "registering a tool able to compile for the platform can solve the problem"
            )

        val (libraryPrefix, librarySuffix) = platform.operatingSystem.library.run {
            when (type) {
                Shared -> sharedPrefix to sharedSuffix
                Static -> staticPrefix to staticSuffix
            }
        }

        val libraryFileName = cProject.libraryName.map { name ->
            "${libraryPrefix}${name}.${librarySuffix}"
        }

        val libraryDirectory = layout.projectGeneratedOutputDir(
            projectName = cProject.name,
            subdirectory = "libraries/${type.name.lowercase()}"
        )

        val libraryFile = libraryDirectory.zip(libraryFileName, Directory::file)
        val compilation = CCompilationImpl(platform, type, libraryFile)
        val compileTaskProvider = createCompileTaskProvider(factory, compilation)

        return CLibraryImpl(compilation, compileTaskProvider)
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
                val settings = project.objects.newInstance<DefaultCInteropSettings>(this)
                configureInterop?.invoke(settings)

                val cInteropDir = layout.projectGeneratedOutputDir(cProject.name, "cinterop")
                val defFileName = "${cProject.name}_${platform.altName}.def"
                val defFileProvider = cInteropDir.map { it.file(defFileName) }

                val defFile = project.objects.fileProperty().apply {
                    set(defFileProvider)
                }

                val defFileOptions = DefFileOptions(
                    excludedFunctions = settings.excludedFunctions,
                    noStringConversion = settings.noStringConversion,
                    outputFile = defFile,
                    libraryFile = library.libraryFile
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