package komple.gradle.project.c

import komple.gradle.kmp.CInteropSettings
import komple.gradle.kmp.DefaultCInteropSettings
import komple.gradle.kmp.GenerateCInteropDefTask
import komple.gradle.kmp.toPlatform
import komple.gradle.project.KompleProjectExtension
import komple.gradle.project.projectDerivedName
import komple.gradle.project.projectGeneratedOutputDir
import komple.gradle.project.registerProjectTask
import komple.gradle.util.camelCased
import komple.gradle.util.dashCased
import komple.gradle.util.pascalCased
import komple.platform.Platform
import komple.project.c.CCompilation
import komple.project.c.CCompileTask
import komple.project.c.CLibraryType
import komple.project.c.CProject
import komple.task.enableTracking
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

    override val kProject: CProject
        get() = cProject

    /**
     * Registers a [Task] obtained from [factory] for [compilation] and returns a [TaskProvider] to
     * it.
     */
    private fun <Task : CCompileTask<*, *>> createCompileTaskProvider(
        factory: CCompileTaskFactory<Task>,
        compilation: CCompilation,
    ): TaskProvider<out Task> {
        val taskName = projectDerivedName(
            projectName = cProject.name,
            postfix = "generate${compilation.libraryType.name}" +
                    "Library${compilation.platform.altName.pascalCased()}"
        )

        return tasks.registerProjectTask(taskName, factory.klass, true) { tracker ->
            description = "Generate a ${compilation.libraryType.name.lowercase()} library for " +
                    "platform ${compilation.platform.name}"

            this.tracker = tracker
            this.cProject = kProject
            this.compilations.add(compilation)
            this.execEnvironment = factory.execEnvironmentProvider.get()

            factory.configure?.invoke(this)
            tracker.enableTracking()
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
            subdirectory = "libraries/${type.name.lowercase()}/${platform.name}"
        )

        val libraryFile = libraryDirectory.zip(libraryFileName, Directory::file)
        val compilation = CCompilationImpl(platform, type, libraryFile)
        val compileTaskProvider = createCompileTaskProvider(factory, compilation)

        val implicitLibraryFile = layout
            .file(compileTaskProvider.map { it.outputs.files.singleFile })

        return CLibraryImpl(compileTaskProvider, implicitLibraryFile, compilation)
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

            cinterops.register(cProject.name.camelCased()) {
                val generateDefTaskProvider = tasks.registerProjectTask<GenerateCInteropDefTask>(
                    name = projectDerivedName(
                        projectName = cProject.name,
                        postfix = "generateCInteropDef${platform.altName.pascalCased()}"
                    )
                )

                val settings = project.objects
                    .newInstance<DefaultCInteropSettings>(this, generateDefTaskProvider)

                configureInterop?.invoke(settings)

                val cInteropDir = layout.projectGeneratedOutputDir(cProject.name, "cinterops")
                val defFileName = "${cProject.name.dashCased()}-${platform.altName}.def"
                val defFileProvider = cInteropDir.map { it.file(defFileName) }

                generateDefTaskProvider.configure {
                    this.cProject = kProject
                    this.platform = platform
                    this.excludedFunctions = settings.excludedFunctions
                    this.noStringConversion = settings.noStringConversion
                    this.definitionFile = defFileProvider
                    this.libraryFile = library.libraryFile
                }

                project.tasks.named(interopProcessingTaskName).configure {
                    dependsOn(generateDefTaskProvider)
                }

                definitionFile = defFileProvider
                includeDirs(cProject.includeDirectories)
            }
        }

        return library
    }
}