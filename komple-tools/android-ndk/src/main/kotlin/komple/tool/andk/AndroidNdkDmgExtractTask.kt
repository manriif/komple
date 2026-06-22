package komple.tool.andk

import komple.task.extract.DmgExtractTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import java.io.File

/**
 * Task responsible for extracting the Android SDK from DMG on macOS.
 */
@CacheableTask
internal abstract class AndroidNdkDmgExtractTask : DmgExtractTask() {

    @get:Input
    abstract val version: Property<String>

    override fun FileSystemOperations.extractContent(
        mountPoint: File,
        outputDirectory: File
    ) {
        val build = version.get().split('.').last()
        val ndkDirectory = mountPoint.resolve("AndroidNDK$build.app/Contents/NDK")

        copy {
            from(ndkDirectory)
            into(outputDirectory)
        }
    }
}