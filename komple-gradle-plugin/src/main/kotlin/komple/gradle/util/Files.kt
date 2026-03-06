package komple.gradle.util

import java.io.File
import java.security.MessageDigest

///////////////////////////////////////////////////////////////////////////
// Hashing
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a new [MessageDigest] for
 */
private inline fun sha256Hex(block: MessageDigest.() -> Unit): String =
    MessageDigest.getInstance("SHA-256")
        .apply(block)
        .digest()
        .joinToString("") { "%02x".format(it) }


/**
 * Append [file]'content to `this`.
 * For directory, files path are included in the digest.
 */
private fun MessageDigest.append(file: File) {
    when {
        file.isFile -> update(file.readBytes())

        file.isDirectory -> file.walkTopDown()
            .filter { it.isFile }
            .sortedBy { it.relativeTo(file).path }
            .forEach { childFile ->
                update(childFile.relativeTo(file).path.toByteArray())
                append(childFile)
            }

        else -> error("File is not a regular file nor a directory")
    }
}

/**
 * Returns the SHA-256 hex hash of `this` file or directory.
 */
internal fun File.sha256(): String {
    return sha256Hex { append(this@sha256) }
}

/**
 * Returns the SHA-256 hex hash of `these` [File]s.
 */
internal fun Collection<File>.sha256(): String {
    if (isEmpty()) {
        return ""
    }

    return sha256Hex {
        forEach(::append)
    }
}