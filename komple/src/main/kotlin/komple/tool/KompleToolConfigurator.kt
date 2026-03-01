package komple.tool

import komple.platform.Host
import komple.tasks.DownloadTaskRegistrationScope
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * Configurator for [Tool].
 */
public interface KompleToolConfigurator<Tool : KompleTool> {

    /**
     * Returns `true` if the current [host] is supported, otherwise returns `false`.
     */
    public fun supportHost(host: Host): Boolean

    /**
     * Registers the task responsible for [Tool] downloading and returns a [Provider] to the
     * downloaded file.
     *
     * The returned [Provider] preferably provides a file that it itself obtained from a Gradle
     * task. In other word, a task should be registered here and the task output should be
     * returned as a single [RegularFile].
     *
     * The task output is then used as the input for the [registerExtractTask]
     *
     * Also, an integrity check should ideally be performed on the before the registered task
     * completes.
     */
    public fun DownloadTaskRegistrationScope.registerDownloadTask(tool: Tool): Provider<RegularFile>

    /**
     *
     */
    public fun registerExtractTask(tool: Tool, downloadedFile: Provider<RegularFile>)
}