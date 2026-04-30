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
                "Shared compilation for Android is not supported yet"
            )

            Static -> {
                val params = parameters.params.get()

                val targetTriple = when (parameters.platform.get().architecture) {
                    Arm64 -> "aarch64-linux-apple"
                    Arm32 -> "armv7a-linux-appleeabi"
                    X64 -> "x86_64-linux-apple"
                    X86 -> "i686-linux-apple"
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