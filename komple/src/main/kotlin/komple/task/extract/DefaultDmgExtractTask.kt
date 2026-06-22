package komple.task.extract

import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import java.io.File

/**
 * Task extracting files from a `.dmg` file by delegating to [extractor].
 */
@CacheableTask
public abstract class DefaultDmgExtractTask : DmgExtractTask() {

    @get:Internal
    public abstract val extractor: Property<DmgContentExtractor>

    override fun FileSystemOperations.extractContent(
        mountPoint: File,
        outputDirectory: File
    ) {
        extractor.get().run {
            extract(mountPoint, outputDirectory)
        }
    }
}