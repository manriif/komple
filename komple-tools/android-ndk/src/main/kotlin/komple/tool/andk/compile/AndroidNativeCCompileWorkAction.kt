package komple.tool.andk.compile

import komple.platform.Architecture
import komple.project.c.CCompileWorkAction
import komple.project.c.CLibraryType
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

internal abstract class AndroidNativeCCompileWorkAction :
    CCompileWorkAction<AndroidNativeCCompileWorkAction.Parameters>() {

    /**
     * Resolves [path] relatively to the Android NDK toolchain directory and returns the absolute
     * path of the resolved file.
     */
    private fun toolchainPath(path: String): String {
        return parameters.toolchainDirectory.map { it.file(path) }.get().asFile.absolutePath
    }

    override fun execute() {
        val compilation = parameters.compilation.get()

        when (compilation.libraryType) {
            CLibraryType.Shared -> throw UnsupportedOperationException(
                "Shared compilation for Android is not supported yet"
            )

            CLibraryType.Static -> {
                val targetTriple = when (compilation.platform.architecture) {
                    Architecture.Arm64 -> "aarch64-linux-android"
                    Architecture.Arm32 -> "armv7a-linux-androideabi"
                    Architecture.X64 -> "x86_64-linux-android"
                    Architecture.X86 -> "i686-linux-android"
                }

                val params = parameters.params.get()

                compileStatic(
                    compilerFlags = arrayOf(
                        toolchainPath("bin/clang"),
                        "-target", "$targetTriple${params.minSdk}",
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

    internal interface Parameters : CCompileWorkAction.Parameters {

        val toolchainDirectory: DirectoryProperty
        val params: Property<AndroidNativeCompilationParams>
    }
}