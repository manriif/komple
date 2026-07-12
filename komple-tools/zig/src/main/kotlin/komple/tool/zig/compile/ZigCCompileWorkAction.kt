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
package komple.tool.zig.compile

import komple.project.c.CCompileWorkAction
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

/**
 * Zig [CCompileWorkAction].
 */
internal abstract class ZigCCompileWorkAction :
    CCompileWorkAction<ZigCCompileWorkAction.Parameters>() {

    /**
     * Returns [os].[minVersion]-[abi].
     */
    private fun versioned(
        os: String,
        minVersion: Provider<String>,
        abi: String = "gnu"
    ): String {
        val version = minVersion.orNull
            ?.takeIf(String::isNotBlank)
            ?.let { ".$it" }
            .orEmpty()

        return "$os$version-$abi"
    }

    override fun execute() {
        val (operatingSystem, architecture) = parameters.platform.get()
        val params = parameters.params.get()

        val osAbi = when (operatingSystem) {
            Linux -> versioned("linux", params.linuxVersionMin)
            Windows -> versioned("windows", params.windowsVersionMin)
            else -> error("Only Linux and Windows are supported but found: $operatingSystem")
        }

        val arch = when (architecture) {
            Arm64 -> "aarch64"
            X64 -> "x86_64"
            else -> error("Only Arm64 and X64 are supported but found: $architecture")
        }

        val targetTriple = "$arch-$osAbi"
        val compilerFlags = arrayOf("zig", "cc", "-target", targetTriple)

        when (parameters.libraryType.get()) {
            Shared -> compileShared(arrayOf(*compilerFlags, "-shared"))

            Static -> compileStatic(
                compilerFlags = compilerFlags,
                archiverFlags = arrayOf("zig", "ar")
            )
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    interface Parameters : CCompileWorkAction.Parameters {

        val params: Property<ZigCompilationParams>
    }
}