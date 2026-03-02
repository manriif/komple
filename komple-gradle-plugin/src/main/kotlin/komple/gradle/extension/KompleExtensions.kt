package komple.gradle.extension

import org.gradle.api.plugins.ExtensionAware

/**
 * Holder for configured tools.
 */
interface KompleToolsExtension : ExtensionAware

/**
 * Extension for dynamically registered tools' configurations.
 */
interface KompleToolsConfigsExtension : ExtensionAware