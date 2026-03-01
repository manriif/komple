package komple.gradle

import komple.KompleInternalApi
import org.gradle.api.file.Directory
import org.gradle.api.invocation.Gradle

///////////////////////////////////////////////////////////////////////////
// Constants
///////////////////////////////////////////////////////////////////////////

/**
 * Identifier of the Komple Gradle plugin which is also the Komple group.
 */
public const val KOMPLE_PLUGIN_ID: String = "io.github.manriif.komple"

/**
 * NAme of the [KompleBaseExtension].
 */
public const val KOMPLE_EXTENSION_NAME: String = "komple"

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
@KompleInternalApi
public val Gradle.kompleChecksumsDirectory: Directory
    get() = kompleCacheDirectory("installs")

/**
 * Returns the directory where the Komple plugin embedded resources resides.
 */
@KompleInternalApi
public val Gradle.kompleResourcesDirectory: Directory
    get() = kompleCacheDirectory("resources")

/**
 * Returns the directory where the tools are downloaded.
 */
@KompleInternalApi
public val Gradle.kompleToolsDownloadsDirectory: Directory
    get() = kompleCacheDirectory("downloads")

/**
 * Returns the directory where the tools are installed.
 */
@KompleInternalApi
public val Gradle.kompleToolsInstallsDirectory: Directory
    get() = kompleCacheDirectory("installs")