package komple.gradle.project

import komple.exec.Command
import komple.exec.ShellEnvironment
import komple.exec.ShellEnvironmentBuilderScope
import komple.gradle.platform.CurrentHost
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.platform.Host
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

/**
 * Default implementation of [ShellEnvironmentBuilderScope].
 */
internal class DefaultShellEnvironmentBuilderScope<Extension : KompleToolExtension>(
    private val context: KompleToolConfigContext<Extension>,
    private val environment: ShellEnvironment,
    override val installDirectory: Provider<Directory>
) : ShellEnvironmentBuilderScope<Extension>,
    HasExtension<Extension> by context,
    ClosableScope() {

    override val host: Host
        get() = notClosed { CurrentHost }

    override val providers: ProviderFactory
        get() = notClosed { context.project.providers }

    override fun path(pathProvider: Provider<String>) =
        environment.notClosed { paths.add(pathProvider) }

    override fun variable(
        name: String,
        valueProvider: Provider<String>
    ) = environment.notClosed { variables.put(name, valueProvider) }

    override fun commandLine(commandProvider: Provider<Command>) =
        environment.notClosed { commands.add(commandProvider) }
}