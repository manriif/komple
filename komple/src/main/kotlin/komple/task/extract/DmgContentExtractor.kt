package komple.task.extract

import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFile
import java.io.File

/**
 * Extractor for DMG content.
 */
public fun interface DmgContentExtractor {

    /**
     * Extracts files from DMG [mountPoint] into [outputDirectory].
     */
    public fun FileSystemOperations.extract(
        mountPoint: File,
        outputDirectory: File
    ): RegularFile
}