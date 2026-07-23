/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package komple.gradle.project.c

import komple.gradle.kmp.CInteropSettings
import komple.gradle.kmp.DefaultCInteropSettings
import komple.gradle.kmp.GenerateCInteropDefTaskInternal
import komple.gradle.kmp.toPlatform
import komple.gradle.project.CompilerNotFoundException
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
import org.gradle.api.model.ObjectFactory
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
    internal val layout: ProjectLayout,
    internal val objects: ObjectFactory
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
        libraryType: CLibraryType,
        platform: Platform
    ): TaskProvider<out Task> {
        val taskName = projectDerivedName(
            projectName = cProject.name,
            postfix = "generate${libraryType.name}Library${platform.altName.pascalCased()}"
        )

        return tasks.registerProjectTask(taskName, factory.klass, true) { tracker ->
            description = "Generate a ${libraryType.name.lowercase()} library for " +
                    "platform ${platform.name}"

            this.tracker = tracker
            this.cProject = kProject
            this.compilation = compilation
            this.execEnvironment = factory.execEnvironmentProvider.get()

            factory.configure?.invoke(this)
            tracker.enableTracking()
        }
    }

    /**
     * Indicates whether there is a registered compiler able to produces a library for [platform].
     */
    public fun hasCompiler(platform: Platform): Boolean =
        compileTaskFactories.any { it.platformFilter(platform) }

    /**
     * Indicates whether there is a registered compiler able to produces a library for [target].
     */
    public fun hasCompiler(target: KotlinNativeTarget): Boolean =
        target.konanTarget.toPlatform()?.let(::hasCompiler) ?: false

    /**
     * Registers a task that generate a library of type [type] for the provided [platform].
     *
     * If no library could be created for [platform] then `null` is returned and no task is
     * registered
     */
    public fun createLibraryOrNull(
        type: CLibraryType,
        platform: Platform
    ): CLibrary? {
        val factory = compileTaskFactories.firstOrNull { it.platformFilter(platform) }
            ?: return null

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

        val compilation = objects.newInstance<CCompilationImpl>().apply {
            this.libraryFile = libraryDirectory.zip(libraryFileName, Directory::file)
            this.libraryType = type
            this.platform = platform
        }

        val compileTaskProvider = createCompileTaskProvider(factory, compilation, type, platform)

        val implicitLibraryFile = layout
            .file(compileTaskProvider.map { it.outputs.files.singleFile })

        return CLibraryImpl(compileTaskProvider, implicitLibraryFile, compilation)
    }

    /**
     * Registers a task that generate a library of type [type] for the provided [platform].
     *
     * @throws CompilerNotFoundException if there is no compiler able to produce a library for
     * [platform].
     */
    public fun createLibrary(
        type: CLibraryType,
        platform: Platform
    ): CLibrary = createLibraryOrNull(type, platform) ?: throw CompilerNotFoundException(
        "No task could be created for compiling for platform $platform, registering a " +
                "tool able to compile for $platform can solve the problem"
    )

    /**
     * Registers a task that generates a library of type [type] for the provided [target].
     * Note that the `LINUX_ARM32_HFP` target is not supported.
     *
     * If no library could be created for [target] then `null` is returned and no task is
     * registered.
     */
    public fun createLibraryOrNull(
        type: CLibraryType,
        target: KotlinNativeTarget,
        configureInterop: (CInteropSettings.() -> Unit)? = null
    ): CLibrary? {
        val platform = target.konanTarget.toPlatform() ?: return null
        val library = createLibraryOrNull(type, platform) ?: return null

        target.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME) {
            compileTaskProvider.configure {
                dependsOn(library.compileTaskProvider)
            }

            cinterops.register(cProject.name.camelCased()) {
                val generateDef = tasks.registerProjectTask<GenerateCInteropDefTaskInternal>(
                    name = projectDerivedName(
                        projectName = cProject.name,
                        postfix = "generateCInteropDef${platform.altName.pascalCased()}"
                    )
                ) {
                    this.cProject = kProject
                    this.platform = platform
                    this.libraryFile = library.libraryFile
                }

                val settings = project.objects
                    .newInstance<DefaultCInteropSettings>(this, generateDef)

                configureInterop?.invoke(settings)

                val cInteropDir = layout.projectGeneratedOutputDir(cProject.name, "cinterops")
                val defFileName = "${cProject.name.dashCased()}-${platform.altName}.def"
                val defFileProvider = cInteropDir.map { it.file(defFileName) }

                generateDef.configure {
                    this.excludedFunctions = settings.excludedFunctions
                    this.noStringConversion = settings.noStringConversion
                    this.definitionFile = defFileProvider
                }

                project.tasks.named(interopProcessingTaskName).configure {
                    dependsOn(generateDef)
                }

                definitionFile = defFileProvider
                includeDirs(cProject.includeDirectories)
            }
        }

        return library
    }

    /**
     * Registers a task that generates a library of type [type] for the provided [target].
     * Note that the `LINUX_ARM32_HFP` target is not supported.
     *
     * @throws CompilerNotFoundException if there is no compiler able to produce a library for
     * [target].
     */
    public fun createLibrary(
        type: CLibraryType,
        target: KotlinNativeTarget,
        configureInterop: (CInteropSettings.() -> Unit)? = null
    ): CLibrary = createLibraryOrNull(type, target, configureInterop)
        ?: throw CompilerNotFoundException(
            "No task could be created for compiling for target ${target.konanTarget}, " +
                    "registering a tool able to compile for ${target.konanTarget} can solve " +
                    "the problem"
        )
}