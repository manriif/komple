@file:Suppress("unused")

package komple.platform

import java.io.Serializable

/**
 * CPU Architectures.
 */
public sealed interface Architecture : Serializable {

    /**
     * Name of the architecture.
     */
    public val name: String

    /**
     * Supported by toolchains for compilation.
     */
    public sealed interface Host : Architecture

    ///////////////////////////////////////////////////////////////////////////
    // ARM
    ///////////////////////////////////////////////////////////////////////////

    public data object Arm64 : Host {

        override val name: String
            get() = "aarch64"

        private fun readResolve(): Any = Arm64
    }

    public data object Arm32 : Architecture {

        override val name: String
            get() = "aarch32"

        private fun readResolve(): Any = Arm32
    }

    ///////////////////////////////////////////////////////////////////////////
    // x86
    ///////////////////////////////////////////////////////////////////////////

    public data object X64 : Host {

        override val name: String
            get() = "x86_64"

        private fun readResolve(): Any = X64
    }

    public data object X86 : Architecture {

        override val name: String
            get() = "x86"

        private fun readResolve(): Any = X86
    }
}