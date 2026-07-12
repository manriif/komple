/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
    internal fun integrity() {
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