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