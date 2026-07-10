package komple.gradle.project.c

import komple.platform.Platform
import komple.project.c.CCompilation
import komple.project.c.CLibraryType
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Implementation of [CCompilation].
 */
internal abstract class CCompilationImpl : CCompilation {

    abstract override val libraryFile: RegularFileProperty
    abstract override val libraryType: Property<CLibraryType>
    abstract override val platform: Property<Platform>
}

/**
 * Implementation of [CLibrary].
 */
internal class CLibraryImpl(
    override val compileTaskProvider: TaskProvider<*>,
    override val libraryFile: Provider<RegularFile>,
    override val compilation: CCompilation
) : CLibrary