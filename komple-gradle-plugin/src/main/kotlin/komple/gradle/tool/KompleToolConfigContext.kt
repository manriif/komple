package komple.gradle.tool

import komple.exec.ExecEnvironment
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Project

/**
 * Context for tool configuration.
 */
internal class KompleToolConfigContext<Extension : KompleToolExtension>(
    val project: Project,
    val toolName: String,
    override val extension: Extension,
    val execEnvironment: ExecEnvironment,
) : HasExtension<Extension>