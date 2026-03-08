package komple.tool.jext.generator

import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

/**
 * Implementation of [JextractBindingGenerator]
 */
internal abstract class JextractBindingGeneratorImpl @Inject constructor(
    options: JextractCommandLineOptions,
    override val generateTaskProvider: TaskProvider<JextractGenerateBindingsTask>
) : JextractBindingGenerator,
    JextractCommandLineOptions by options