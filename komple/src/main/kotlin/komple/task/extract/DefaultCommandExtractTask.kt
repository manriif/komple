package komple.task.extract

import komple.exec.Command
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import java.io.File

/**
 * Task extracting files using a command obtained via [provider].
 */
@CacheableTask
public abstract class DefaultCommandExtractTask : CommandExtractTask() {

    @get:Internal
    public abstract val provider: Property<ExtractCommandProvider>

    override fun buildCommand(
        inputDirectory: File,
        outputDirectory: File
    ): Command = provider.get().createCommand(inputDirectory, outputDirectory)
}