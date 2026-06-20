@file:Suppress("unused")

package komple.platform

import java.io.Serializable

/**
 * Operating Systems.
 */
public sealed interface OperatingSystem : Serializable {

    /**
     * Primary name.
     */
    public val name: String

    /**
     * Alternative name.
     */
    public val altName: String
        get() = name

    /**
     * Library information.
     */
    public val library: Library

    /**
     * Supported by toolchains for compilation.
     */
    public sealed interface Host : OperatingSystem

    ///////////////////////////////////////////////////////////////////////////
    // Darwin
    ///////////////////////////////////////////////////////////////////////////

    public sealed class Darwin : OperatingSystem {

        final override val library: Library
            get() = Library.Darwin
    }

    public data object MacOS : Darwin(), Host {

        override val name: String
            get() = "macos"

        private fun readResolve(): Any = MacOS
    }

    public sealed class IOS : Darwin() {

        public data object Simulator : IOS() {

            override val name: String
                get() = "ios-simulator"

            private fun readResolve(): Any = Simulator
        }

        public companion object Default : IOS() {

            override val name: String
                get() = "ios"

            private fun readResolve(): Any = Default
        }
    }

    public sealed class TvOS : Darwin() {

        public data object Simulator : TvOS() {

            override val name: String
                get() = "tvos-simulator"

            private fun readResolve(): Any = Simulator
        }

        public companion object Default : TvOS() {

            override val name: String
                get() = "tvos"

            private fun readResolve(): Any = Default
        }
    }

    public sealed class WatchOS : Darwin() {

        public data object Device : WatchOS() {

            override val name: String
                get() = "watchos-device"

            private fun readResolve(): Any = Device
        }

        public data object Simulator : WatchOS() {

            override val name: String
                get() = "watchos-simulator"

            private fun readResolve(): Any = Simulator
        }

        public companion object Default : WatchOS() {

            override val name: String
                get() = "watchos"

            private fun readResolve(): Any = Default
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Linux
    ///////////////////////////////////////////////////////////////////////////

    public sealed class LinuxLike : OperatingSystem {

        final override val library: Library
            get() = Library.Linux
    }

    public data object Android : LinuxLike() {

        override val name: String
            get() = "android"

        private fun readResolve(): Any = Android
    }

    public data object Linux : LinuxLike(), Host {

        override val name: String
            get() = "linux"

        private fun readResolve(): Any = Linux
    }

    ///////////////////////////////////////////////////////////////////////////
    // Windows
    ///////////////////////////////////////////////////////////////////////////

    public data object Windows : Host {

        override val name: String
            get() = "windows"

        override val altName: String
            get() = "mingw"

        override val library: Library
            get() = Library.MinGw

        private fun readResolve(): Any = Windows
    }
}