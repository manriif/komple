package komple.tool.jext.generator

import komple.project.c.CProject
import org.gradle.api.Action
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Implementation of [JextractBindingGenerator]
 */
internal class JextractBindingGeneratorImpl(
    val generatorName: String,
    override val options: JextractCommandLineOptions,
    override val cProject: CProject,
    override val generateTaskProvider: TaskProvider<JextractGenerateBindingsTask>,
    override val generateDirectory: Provider<Directory>
) : JextractBindingGenerator {

    override fun getName(): String {
        return generatorName
    }

    override fun options(configuration: Action<JextractCommandLineOptions>) {
        return configuration.execute(options)
    }
}