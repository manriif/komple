package komple.gradle.tool.install

import komple.tool.install.Inputs
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * Default implementation of [Inputs].
 */
internal class DefaultInputs(
    override val files: Provider<FileCollection>,
    private val layout: ProjectLayout
) : Inputs {

    override val file: Provider<RegularFile>
        get() = layout.file(files.map { it.singleFile })
}