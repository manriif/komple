package komple.tool.configurator

import komple.tool.compile.CompilationBuilderScope
import komple.tool.compile.ExecEnvironmentBuilderScope
import komple.tool.extension.KompleToolExtension
import komple.tool.task.ExtractTaskRegistrationScope
import komple.tool.task.InstallTaskRegistrationScope
import komple.tool.task.IntegrityTaskRegistrationScope
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskProvider

/**
 * Base for implementor of [KompleToolConfigurator] with a default implementation for common properties and
 * methods.
 */
public abstract class DefaultKompleToolConfigurator<Extension : KompleToolExtension>(
    @Internal protected val toolName: String
) : KompleToolConfigurator<Extension> {

    override fun getName(): String {
        return toolName
    }

    override fun IntegrityTaskRegistrationScope<Extension>.registerIntegrityTask(): TaskProvider<*> {
        return skipIntegrityCheck()
    }

    override fun ExtractTaskRegistrationScope<Extension>.registerExtractTask(): TaskProvider<*> {
        return skipExtraction()
    }

    override fun InstallTaskRegistrationScope<Extension>.registerInstallTask(): TaskProvider<*> {
        return skipInstallation()
    }

    override fun CompilationBuilderScope<Extension>.configureCompilation() {}

    override fun ExecEnvironmentBuilderScope<Extension>.configureEnvironment() {}
}