/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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