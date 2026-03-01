package komple.gradle.extension

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer

/**
 * Casts `this` object as [ExtensionAware] and returns its [ExtensionContainer].
 */
internal inline val Any.extensions: ExtensionContainer
    get() = (this as ExtensionAware).extensions