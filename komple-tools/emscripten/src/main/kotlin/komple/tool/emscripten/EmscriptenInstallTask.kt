package komple.tool.emscripten

import komple.exec.Command
import komple.platform.OperatingSystem
import komple.platform.OperatingSystem.Linux
import komple.platform.OperatingSystem.MacOS
import komple.platform.OperatingSystem.Windows
import komple.task.install.CommandInstallTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * Task responsible for installing Emscripten.
 */
@DisableCachingByDefault(because = "Emscripten is too large, the tracker approach is preferred")
internal abstract class EmscriptenInstallTask : CommandInstallTask() {

    @get:Internal
    abstract val operatingSystem: Property<OperatingSystem.Host>

    @get:Input
    abstract val emscriptenVersion: Property<String>

    override fun buildCommand(outputDirectory: File): Command {
        val version = emscriptenVersion.get()

        val emsdk = when (operatingSystem.get()) {
            MacOS, Linux -> "./emsdk"
            Windows -> "emsdk.bat"
        }

        return Command(emsdk, "install", version) {
            then(emsdk, "activate", version)
        }
    }
}