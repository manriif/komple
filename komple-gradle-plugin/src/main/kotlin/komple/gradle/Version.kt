package komple.gradle

import komple.gradle.extension.KompleRootExtension

/**
 * Alias for Komple in version catalog.
 */
internal const val KOMPLE_ALIAS = "komple"

/**
 * Extracts the Komple version from resources.
 */
internal fun extractKompleVersion(): String {
    return KompleRootExtension::class.java.classLoader
        .getResource("version.txt")
        ?.readText()
        ?.trim()
        ?: error("Failed to read Komple version")
}