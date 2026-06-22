package komple.task.integrity

import komple.task.singleFile
import org.gradle.api.GradleException
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
        val inputFile = inputDirectory.get().asFile.singleFile
        val messageDigest = MessageDigest.getInstance(algorithm.get().toMessageDigestConstant())
        val expectedChecksum = checksum.get()
        val actualChecksum: String

        try {
            inputFile.inputStream().use { stream ->
                val buffer = ByteArray(1024)
                var read: Int

                while ((stream.read(buffer).also { read = it }) != -1) {
                    messageDigest.update(buffer, 0, read)
                }

                actualChecksum = messageDigest.digest()
                    .joinToString("") { "%02x".format(it) }
            }
        } catch (exception: Throwable) {
            didWork = false
            return logger.error("Failed to compute checksum", exception)
        }

        if (actualChecksum == expectedChecksum) {
            didWork = true
        } else {
            didWork = false
            throw GradleException("Checksum mismatch: $expectedChecksum vs $actualChecksum")
        }
    }
}