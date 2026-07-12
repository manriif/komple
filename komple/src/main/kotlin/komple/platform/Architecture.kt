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
 * CPU Architectures.
 */
public sealed interface Architecture : Serializable {

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
     * Supported by toolchains for compilation.
     */
    public sealed interface Host : Architecture

    ///////////////////////////////////////////////////////////////////////////
    // ARM
    ///////////////////////////////////////////////////////////////////////////

    public data object Arm64 : Host {

        override val name: String
            get() = "aarch64"

        override val altName: String
            get() = "arm64"

        private fun readResolve(): Any = Arm64
    }

    public data object Arm32 : Architecture {

        override val name: String
            get() = "aarch32"

        override val altName: String
            get() = "arm32"

        private fun readResolve(): Any = Arm32
    }

    ///////////////////////////////////////////////////////////////////////////
    // x86
    ///////////////////////////////////////////////////////////////////////////

    public data object X64 : Host {

        override val name: String
            get() = "x86_64"

        override val altName: String
            get() = "x64"

        private fun readResolve(): Any = X64
    }

    public data object X86 : Architecture {

        override val name: String
            get() = "x86"

        private fun readResolve(): Any = X86
    }
}