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

        override val name: String
            get() = "ios"

        public data object Device : IOS() {
            private fun readResolve(): Any = Device
        }

        public data object Simulator : IOS() {
            private fun readResolve(): Any = Simulator
        }
    }

    public sealed class TvOS : Darwin() {

        override val name: String
            get() = "tvos"

        public data object Device : TvOS() {
            private fun readResolve(): Any = Device
        }

        public data object Simulator : TvOS() {
            private fun readResolve(): Any = Simulator
        }
    }

    public sealed class WatchOS : Darwin() {

        override val name: String
            get() = "watchos"

        public data object Device : WatchOS() {
            private fun readResolve(): Any = Device
        }

        public data object DeviceGen2 : WatchOS() {
            private fun readResolve(): Any = DeviceGen2
        }

        public data object Simulator : WatchOS() {
            private fun readResolve(): Any = Simulator
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