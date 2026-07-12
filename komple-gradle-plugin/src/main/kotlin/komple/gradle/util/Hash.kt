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
package komple.gradle.util

import net.jpountz.xxhash.StreamingXXHash64
import net.jpountz.xxhash.XXHashFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectOutputStream
import java.io.Serializable

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
 * Append [value]'s content to this hasher.
 */
internal fun StreamingXXHash64.append(value: Serializable) {
    val bytesStream = ByteArrayOutputStream()
    val objectStream = ObjectOutputStream(bytesStream)

    objectStream.writeObject(value)
    val bytes = bytesStream.toByteArray()

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