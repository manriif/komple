package komple.tool.emscripten

import komple.exec.Command
import komple.task.install.CommandInstallTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import java.io.File

/**
 * Task responsible for installing Emscripten.
 */
@CacheableTask
internal abstract class EmscriptenInstallTask : CommandInstallTask() {

    @get:Input
    abstract val emscriptenVersion: Property<String>

    @get:Input
    abstract val emsdkExecutable: Property<String>

    override fun buildCommand(outputDirectory: File): Command {
        val version = emscriptenVersion.get()
        val emsdk = emsdkExecutable.get()

        return Command(emsdk, "install", version) {
            then(emsdk, "activate", version)
        }
    }
}