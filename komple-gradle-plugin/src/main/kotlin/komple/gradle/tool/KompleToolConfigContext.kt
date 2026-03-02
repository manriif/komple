package komple.gradle.tool

import komple.gradle.Komple

/**
 * Context for tool configuration.
 */
internal class KompleToolConfigContext(
    val komple: Komple,
    val toolName: String
)