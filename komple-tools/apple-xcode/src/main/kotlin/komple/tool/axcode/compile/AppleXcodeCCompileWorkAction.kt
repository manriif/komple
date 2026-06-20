package komple.tool.axcode.compile

import komple.exec.executeWithOutput
import komple.platform.OperatingSystem
import komple.project.c.CCompileWorkAction
import org.gradle.api.provider.Property

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
        flag: String
    ): Array<String> {
        val sdkPath = commandExecutor.executeWithOutput("xcrun", "--sdk", sdk, "--show-sdk-path")
        return arrayOf("clang", "-arch", arch, "-isysroot", sdkPath, flag)
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
                        flag = "-mmacosx-version-min=${params.versionMinMacos.get()}"
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
                        flag = "-mios-version-min=${params.versionMinIos.get()}"
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
                        flag = "-mtvos-version-min=${params.versionMinTvos.get()}"
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
                        flag = "-mwatchos-version-min=${params.versionMinWatchos.get()}"
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