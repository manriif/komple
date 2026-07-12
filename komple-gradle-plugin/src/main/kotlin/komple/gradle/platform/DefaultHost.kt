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
package komple.gradle.platform

import komple.platform.Architecture
import komple.platform.Host
import komple.platform.OperatingSystem

/**
 * Default implementation of [Host].
 */
private data class DefaultHost(
    override val operatingSystem: OperatingSystem.Host,
    override val architecture: Architecture.Host
) : Host

/**
 * Current host.
 */
internal val CurrentHost: Host = DefaultHost(
    operatingSystem = org.gradle.internal.os.OperatingSystem.current().run {
        when {
            isMacOsX -> OperatingSystem.MacOS
            isWindows -> OperatingSystem.Windows
            isLinux -> OperatingSystem.Linux
            else -> error("Unsupported operation system: $this")
        }
    },
    architecture = when (val architecture = System.getProperty("os.arch").lowercase()) {
        "aarch64", "arm64" -> Architecture.Arm64
        "x86_64", "amd64" -> Architecture.X64
        else -> error("Unsupported CPU architecture: $architecture")
    }
)