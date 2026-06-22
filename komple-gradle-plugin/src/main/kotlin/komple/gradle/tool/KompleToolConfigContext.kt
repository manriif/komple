package komple.gradle.tool

import komple.gradle.exec.ExecEnvironmentProvider
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
    val execEnvironmentProvider: ExecEnvironmentProvider,
) : HasExtension<Extension>