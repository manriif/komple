package komple.gradle.util

import net.jpountz.xxhash.StreamingXXHash64
import net.jpountz.xxhash.XXHashFactory
import java.io.File

private const val HASH_SEED = 0x9747b28cL
private val HashFactory = XXHashFactory.fastestInstance()

/**
 * Returns a new xx64 hash that is populated within [block].
 */
internal inline fun xxHash64(block: StreamingXXHash64.() -> Unit): String = HashFactory
    .newStreamingHash64(HASH_SEED)
    .apply(block)
    .run { "%016x".format(value) }

/**
 * Append [file]'s content to this hasher.
 * For directory, files path are included in the digest.
 */
internal fun StreamingXXHash64.append(file: File) {
    when {
        file.isFile -> file.inputStream().use { stream ->
            val buffer = ByteArray(8 * 1024)
            var read: Int

            while (stream.read(buffer).also { read = it } != -1) {
                update(buffer, 0, read)
            }
        }

        file.isDirectory -> file.walkTopDown()
            .filter { it.isFile }
            .sortedBy { it.relativeTo(file).path }
            .forEach { childFile ->
                val relativePathBytes = childFile.relativeTo(file).path.toByteArray()
                update(relativePathBytes, 0, relativePathBytes.size)
                append(childFile)
            }

        else -> error("File is not a regular file nor a directory")
    }
}

/**
 * Append [value]'s content to this hasher.
 */
internal fun StreamingXXHash64.append(value: String) {
    val bytes = value.toByteArray()
    update(bytes, 0, bytes.size)
}

/**
 * Invokes [block] for each element of [elements].
 */
internal inline fun <E> StreamingXXHash64.append(
    elements: List<E>,
    block: StreamingXXHash64.(E) -> Unit
) {
    for (element in elements) {
        block(element)
    }
}