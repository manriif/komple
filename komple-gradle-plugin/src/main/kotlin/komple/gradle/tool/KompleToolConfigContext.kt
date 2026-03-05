package komple.gradle.tool

import komple.gradle.extension.KompleRootProjectExtension
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Project

/**
 * Context for tool configuration.
 */
internal class KompleToolConfigContext<Extension : KompleToolExtension>(
    val project: Project,
    val rootExtension: KompleRootProjectExtension,
    val toolName: String,
    override val extension: Extension
) : HasExtension<Extension>