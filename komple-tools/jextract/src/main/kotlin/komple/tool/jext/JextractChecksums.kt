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
package komple.tool.jext

import org.gradle.api.provider.Property

/**
 * SHA-256 checksums for Jextract integrity check.
 */
public interface JextractChecksums {

    /**
     * Linux ARM64 host checksum.
     */
    public val linuxAarch64: Property<String>

    /**
     * Linux X64 host checksum.
     */
    public val linuxX64: Property<String>

    /**
     * MacOS ARM64 host checksum.
     */
    public val macosAarch64: Property<String>

    /**
     * MacOS X64 host checksum.
     */
    public val macosX64: Property<String>

    /**
     * Windows X64 host checksum.
     */
    public val windowsX64: Property<String>
}