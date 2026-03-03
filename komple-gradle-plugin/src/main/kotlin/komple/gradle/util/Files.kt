package komple.gradle.util

import java.io.File
import java.security.MessageDigest

///////////////////////////////////////////////////////////////////////////
// Hashing
///////////////////////////////////////////////////////////////////////////

/**
 * Returns the SHA-256 hash of this file or directory.
 */
internal fun File.sha256(): String {
    val messageDigest = when {
        isFile -> MessageDigest.getInstance("SHA-256").apply {
            update(readBytes())
        }

        isDirectory -> MessageDigest.getInstance("SHA-256").apply {
            walkTopDown()
                .filter { it.isFile }
                .sortedBy { it.relativeTo(this@sha256).path }
                .forEach { file ->
                    update(file.relativeTo(this@sha256).path.toByteArray())
                    update(file.readBytes())
                }
        }

        else -> error("File is not a regular file nor a directory")
    }

    return messageDigest.digest().joinToString("") { "%02x".format(it) }
}

/**
 * Returns the SHA-256 hash of this files.
 */
internal fun List<File>.sha256(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256").apply {
        sortedBy { it.path }.forEach { file ->
            update(file.path.toByteArray())
            update(file.readBytes())
        }
    }

    return messageDigest.digest().joinToString("") { "%02x".format(it) }
}