package komple.tool.compile

import komple.exec.ExecEnvironmentBuilder
import komple.extension.HasExtension
import komple.platform.HasHost
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

/**
 * Scope for the execution environment building.
 */
public interface ExecEnvironmentBuilderScope :
    ExecEnvironmentBuilder,
    HasExtension,
    HasHost {

    /**
     * Returns the project's [ProviderFactory].
     */
    public val providers: ProviderFactory

    /**
     * Provides the directory where the tool is installed.
     */
    public val installDirectory: Provider<Directory>
}