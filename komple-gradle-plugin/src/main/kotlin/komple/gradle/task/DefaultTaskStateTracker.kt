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
package komple.gradle.task

import komple.gradle.util.append
import komple.gradle.util.xxHash64
import komple.task.TaskStateTracker
import net.jpountz.xxhash.StreamingXXHash64
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskInputs
import org.gradle.kotlin.dsl.property
import java.io.File
import java.io.Serializable

/**
 * Default implementation of [TaskStateTracker].
 * Tracking is disabled by default.
 */
internal class DefaultTaskStateTracker(
    private val checksumDirectory: Directory,
    private val logger: Logger,
    objects: ObjectFactory
) : TaskStateTracker {

    override var isInputTrackingEnabled: Boolean = false
    override var isOutputTrackingEnabled: Boolean = false

    val inputs = objects.property<TaskInputs>()
    val outputFiles = objects.property<FileCollection>()

    /**
     * Returns the checksum file for [name].
     */
    private fun checksumFile(name: String): File = checksumDirectory.file(name).asFile

    /**
     * Invokes [block] passing it a hasher and returns its result or `null` if hashing fails.
     */
    private inline fun computeChecksum(block: StreamingXXHash64.() -> Unit): String? = try {
        xxHash64(block)
    } catch (exception: Throwable) {
        logger.warn("Failed to compute checksum", exception)
        null
    }

    /**
     * Appends [value] to this hasher.
     */
    private fun StreamingXXHash64.appendInputValue(value: Any?): Unit = when (value) {
        null -> append("null")
        is String -> append(value)
        is Enum<*> -> append(value.name)
        is Long, is Int, is Short, is Byte, is Double, is Float -> append(value.toString())
        is File -> append(value)
        is FileCollection -> value.forEach(::append)
        is Collection<*> -> value.forEach { appendInputValue(it) }

        is Map<*, *> -> value.forEach { (key, value) ->
            appendInputValue(key)
            appendInputValue(value)
        }

        is Serializable -> append(value)
        else -> logger.info("Unsupported input type: ${value::class}")
    }

    /**
     * Computes the checksum for the inputs.
     */
    private fun computeInputsChecksum(): String? {
        val inputs = inputs.orNull ?: return EMPTY_CHECKSUM
        val properties = inputs.properties
        val files = inputs.files

        if (properties.isEmpty() && (files.isEmpty || files.none(File::exists))) {
            return EMPTY_CHECKSUM
        }

        return computeChecksum {
            files.forEach(::append)

            properties.forEach { entry ->
                append(entry.key)
                appendInputValue(entry.value)
            }
        }
    }

    /**
     * Computes the checksum for the output files.
     */
    private fun computeOutputsChecksum(): String? {
        val files = outputFiles.orNull ?: return EMPTY_CHECKSUM

        if (files.isEmpty || files.none(File::exists)) {
            return EMPTY_CHECKSUM
        }

        return computeChecksum {
            files.forEach(::append)
        }
    }

    /**
     * Returns whether the checksum supplied by [compute] differs from the one for [fileName].
     */
    private inline fun checksumChanged(
        fileName: String,
        compute: () -> String?
    ): Boolean {
        val file = checksumFile(fileName)

        if (!file.exists()) {
            return true
        }

        val newChecksum = compute() ?: return false
        val oldChecksum = file.readText()

        return newChecksum != oldChecksum
    }

    /**
     * Returns whether the checksum supplied by [compute] differs from the one for [fileName].
     */
    private inline fun updateChecksum(
        fileName: String,
        compute: () -> String?
    ) {
        val file = checksumFile(fileName)

        val newChecksum = compute() ?: run {
            if (file.exists()) {
                file.delete()
            }

            return
        }

        if (file.exists()) {
            val oldChecksum = file.readText()

            if (oldChecksum == newChecksum) {
                return
            }
        }

        file.parentFile.mkdirs()
        file.writeText(newChecksum)
    }

    override fun hasInputsChanged(): Boolean =
        !isInputTrackingEnabled || checksumChanged(CHECKSUM_INPUTS, ::computeInputsChecksum)

    override fun hasOutputsChanged(): Boolean =
        !isOutputTrackingEnabled || checksumChanged(CHECKSUM_OUTPUTS, ::computeOutputsChecksum)

    /**
     * Computes the checksums of inputs and output files and writes them to the corresponding files.
     */
    fun updateChecksums() {
        if (isInputTrackingEnabled) {
            updateChecksum(CHECKSUM_INPUTS, ::computeInputsChecksum)
        }

        if (isOutputTrackingEnabled) {
            updateChecksum(CHECKSUM_OUTPUTS, ::computeOutputsChecksum)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Companion
    ///////////////////////////////////////////////////////////////////////////

    companion object {

        private const val EMPTY_CHECKSUM = "<empty>"
        private const val CHECKSUM_INPUTS = "inputs"
        private const val CHECKSUM_OUTPUTS = "outputs"
    }
}