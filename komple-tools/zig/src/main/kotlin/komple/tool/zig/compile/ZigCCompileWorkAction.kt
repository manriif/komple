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