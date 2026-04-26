package komple.gradle.exec

import komple.gradle.extension.KompleRootProjectExtension

/**
 * Configures the [extension] command executors.
 */
internal fun configureCommandExecutors(extension: KompleRootProjectExtension) {
    extension.commandExecutors.run {
        configureEach {
            commandInterpreter.convention(extension.commandInterpreter)
        }
    }
}