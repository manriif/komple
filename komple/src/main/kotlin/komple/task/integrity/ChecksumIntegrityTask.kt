package komple.task.integrity

import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.security.MessageDigest

/**
 * Task checking the integrity of a single file.
 */
@CacheableTask
public abstract class ChecksumIntegrityTask : IntegrityTask() {

    /**
     * Expected checksum value.
     */
    @get:Input
    public abstract val checksum: Property<String>

    /**
     * Algorithm to use for hash generation.
     */
    @get:Input
    public abstract val algorithm: Property<DigestAlgorithm>

    @TaskAction
    public fun integrity() {
        val inputFile = inputDirectory.get().singleFile.get().asFile
        val messageDigest = MessageDigest.getInstance(algorithm.get().toMessageDigestConstant())
        val calculatedChecksum: String

        try {
            inputFile.inputStream().use { stream ->
                val buffer = ByteArray(1024)
                var read: Int

                while ((stream.read(buffer).also { read = it }) != -1) {
                    messageDigest.update(buffer, 0, read)
                }

                calculatedChecksum = messageDigest.digest()
                    .joinToString("") { "%02x".format(it) }
            }
        } catch (exception: Throwable) {
            didWork = false
            return logger.warn("Failed to compute checksum", exception)
        }

        if (calculatedChecksum == checksum.get()) {
            didWork = true
        } else {
            didWork = false
            logger.warn("Checksum differs")
        }
    }
}