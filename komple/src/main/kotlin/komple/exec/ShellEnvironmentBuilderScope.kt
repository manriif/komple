package komple.exec

import komple.platform.HasHost
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

/**
 * Scope for the execution environment building.
 */
public interface ShellEnvironmentBuilderScope<Extension : KompleToolExtension> :
    ShellEnvironmentBuilder,
    HasExtension<Extension>,
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