package komple.gradle

import org.gradle.api.file.Directory
import org.gradle.api.invocation.Gradle

/**
 * Name of the directory where komple cached files resides.
 */
private const val KOMPLE_CACHE_DIRECTORY: String = ".komple"

///////////////////////////////////////////////////////////////////////////
// Directories
///////////////////////////////////////////////////////////////////////////

/**
 * Returns the directory where the tools are downloaded.
 */
private fun Gradle.kompleCacheDirectory(subdirectory: String): Directory {
    return rootProject.layout.projectDirectory.dir("$KOMPLE_CACHE_DIRECTORY/$subdirectory")
}

/**
 * Returns the directory where the checksums are written.
 */
internal val Gradle.kompleChecksumsDirectory: Directory
    get() = kompleCacheDirectory("checksums")

/**
 * Returns the directory where the Komple plugin embedded resources resides.
 */
internal val Gradle.kompleResourcesDirectory: Directory
    get() = kompleCacheDirectory("resources")

/**
 * Returns the directory where the tools are downloaded.
 */
internal val Gradle.kompleToolsDownloadsDirectory: Directory
    get() = kompleCacheDirectory("downloads")

/**
 * Returns the directory where the tools are installed.
 */
internal val Gradle.kompleToolsInstallsDirectory: Directory
    get() = kompleCacheDirectory("installs")