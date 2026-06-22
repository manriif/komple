package komple.tool.configurator

import komple.exec.ShellEnvironmentBuilderScope
import komple.project.ProjectConfigurationScope
import komple.tool.extension.KompleToolExtension
import komple.tool.task.DownloadTaskRegistrationScope
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

    override fun DownloadTaskRegistrationScope<Extension>.registerDownloadTask(): TaskProvider<*> {
        return skip()
    }

    override fun IntegrityTaskRegistrationScope<Extension>.registerIntegrityTask(): TaskProvider<*> {
        return skip()
    }

    override fun ExtractTaskRegistrationScope<Extension>.registerExtractTask(): TaskProvider<*> {
        return skip()
    }

    override fun InstallTaskRegistrationScope<Extension>.registerInstallTask(): TaskProvider<*> {
        return skip()
    }

    override fun ShellEnvironmentBuilderScope<Extension>.configureEnvironment() {}

    override fun ProjectConfigurationScope<Extension>.configureProject() {}
}