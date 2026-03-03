package komple.gradle.tool.compile

import komple.exec.Command
import komple.gradle.Komple
import komple.gradle.exec.ExecEnvironment
import komple.gradle.platform.CurrentHost
import komple.gradle.util.ClosableScope
import komple.platform.Host
import komple.tool.compile.ExecEnvironmentBuilderScope
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import kotlin.reflect.KClass

/**
 * Default implementation of [ExecEnvironmentBuilderScope].
 */
internal class DefaultExecEnvironmentBuilderScope(
    private val komple: Komple,
    private val environment: ExecEnvironment,
    override val installDirectory: Provider<Directory>
) : ExecEnvironmentBuilderScope,
    ClosableScope() {

    override val host: Host
        get() = notClosed { CurrentHost }

    override val providers: ProviderFactory
        get() = notClosed { komple.project.providers }

    override fun <Extension : Any> extension(type: KClass<Extension>): Extension =
        komple.notClosed { retrieve(type) }

    override fun path(pathProvider: Provider<String>) =
        environment.notClosed { paths.add(pathProvider) }

    override fun variable(
        name: String,
        valueProvider: Provider<String>
    ) = environment.notClosed { variables.put(name, valueProvider) }

    override fun commandLine(commandProvider: Provider<Command>) =
        environment.notClosed { commands.add(commandProvider) } // TODO filter
}