package komple.task.install

import komple.exec.Command
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Internal
import java.io.File

/**
 * Task configuring tool files using a command obtained via [provider].
 */
@CacheableTask
public abstract class DefaultCommandInstallTask : CommandInstallTask() {

    @get:Internal
    public abstract val provider: Property<InstallCommandProvider>

    override fun buildCommand(outputDirectory: File): Command =
        provider.get().createCommand(outputDirectory)
}