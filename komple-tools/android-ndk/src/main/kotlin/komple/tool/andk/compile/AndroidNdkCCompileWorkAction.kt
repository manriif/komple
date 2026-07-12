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
package komple.tool.andk.compile

import komple.project.c.CCompileWorkAction
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

/**
 * Android Native [CCompileWorkAction].
 */
internal abstract class AndroidNdkCCompileWorkAction :
    CCompileWorkAction<AndroidNdkCCompileWorkAction.Parameters>() {

    /**
     * Resolves [path] relatively to the Android NDK toolchain directory and returns the absolute
     * path to the resolved file.
     */
    private fun toolchainPath(path: String): String {
        return parameters.toolchainDirectory.map { it.file(path) }.get().asFile.absolutePath
    }

    override fun execute() {
        when (parameters.libraryType.get()) {
            Shared -> throw UnsupportedOperationException(
                "Shared compilation for Android is not yet supported"
            )

            Static -> {
                val params = parameters.params.get()

                val targetTriple = when (parameters.platform.get().architecture) {
                    Arm64 -> "aarch64-linux-android"
                    Arm32 -> "armv7a-linux-androideabi"
                    X64 -> "x86_64-linux-android"
                    X86 -> "i686-linux-android"
                }

                compileStatic(
                    compilerFlags = arrayOf(
                        toolchainPath("bin/clang"),
                        "-target", "$targetTriple${params.minSdk.get()}",
                        "--sysroot=${toolchainPath("sysroot")}"
                    ),
                    archiverFlags = arrayOf(
                        toolchainPath("bin/llvm-ar")
                    )
                )
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    interface Parameters : CCompileWorkAction.Parameters {

        val toolchainDirectory: DirectoryProperty
        val params: Property<AndroidNdkCompilationParams>
    }
}