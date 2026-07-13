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
package komple.tool.axcode.compile

import komple.exec.executeWithOutput
import komple.platform.OperatingSystem
import komple.project.c.CCompileWorkAction
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

/**
 * Apple Xcode [CCompileWorkAction].
 */
internal abstract class AppleXcodeCCompileWorkAction :
    CCompileWorkAction<AppleXcodeCCompileWorkAction.Parameters>() {

    /**
     * Returns the compiler flags for static Xcode compilation.
     */
    private fun xcodeStaticCompilerFlags(
        arch: String,
        sdk: String,
        platform: String,
        version: Provider<String>
    ): Array<String> {
        val sdkPath = commandExecutor.executeWithOutput("xcrun", "--sdk", sdk, "--show-sdk-path")
        var target = "$arch-apple-$platform${version.get()}"

        if (sdk.endsWith("simulator")) {
            target += "-simulator"
        }

        return arrayOf("xcrun", "clang", "-target", target, "-isysroot", sdkPath)
    }

    override fun execute() {
        val (operatingSystem, architecture) = parameters.platform.get()

        check(operatingSystem is OperatingSystem.Darwin) {
            "Unexpected non Darwin operating system: $operatingSystem"
        }

        when (parameters.libraryType.get()) {
            Shared -> when (operatingSystem) {
                MacOS -> {
                    val arch = when (architecture) {
                        Arm64 -> "arm64"
                        X64 -> "x86_64"
                        else -> error("Unsupported macOS architecture: $architecture")
                    }

                    compileShared(arrayOf("clang", "-dynamiclib", "-arch", arch))
                }

                else -> throw UnsupportedOperationException(
                    "Shared compilation is only supported for macOS libraries"
                )
            }

            Static -> {
                val params = parameters.params.get()

                val compilerFlags = when (operatingSystem) {
                    MacOS -> xcodeStaticCompilerFlags(
                        arch = when (architecture) {
                            Arm64 -> "arm64"
                            X64 -> "x86_64"
                            else -> error("Unsupported macOS architecture: $architecture")
                        },
                        sdk = "macosx",
                        platform = "macos",
                        version = params.versionMinMacos
                    )

                    is IOS -> xcodeStaticCompilerFlags(
                        arch = when (architecture) {
                            Arm64 -> "arm64"
                            X64 -> "x86_64"
                            else -> error("Unsupported iOS architecture: $architecture")
                        },
                        sdk = when (operatingSystem) {
                            Default -> "iphoneos"
                            Simulator -> "iphonesimulator"
                        },
                        platform = "ios",
                        version = params.versionMinIos
                    )

                    is TvOS -> xcodeStaticCompilerFlags(
                        arch = when (architecture) {
                            Arm64 -> "arm64"
                            X64 -> "x86_64"
                            else -> error("Unsupported tvOS architecture: $architecture")
                        },
                        sdk = when (operatingSystem) {
                            Default -> "appletvos"
                            Simulator -> "appletvsimulator"
                        },
                        platform = "tvos",
                        version = params.versionMinTvos
                    )

                    is WatchOS -> xcodeStaticCompilerFlags(
                        arch = when (architecture) {
                            Arm32 -> "armv7k"
                            X64 -> "x86_64"

                            Arm64 -> when (operatingSystem) {
                                Default -> "arm64_32"
                                Device, Simulator -> "arm64"
                            }

                            else -> error("Unsupported watchOS architecture: $architecture")
                        },
                        sdk = when (operatingSystem) {
                            Default, Device -> "watchos"
                            Simulator -> "watchsimulator"
                        },
                        platform = "watchos",
                        version = params.versionMinWatchos
                    )
                }

                compileStatic(
                    compilerFlags = compilerFlags,
                    archiverFlags = arrayOf("ar")
                )
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    interface Parameters : CCompileWorkAction.Parameters {

        val params: Property<AppleXcodeCompilationParams>
    }
}