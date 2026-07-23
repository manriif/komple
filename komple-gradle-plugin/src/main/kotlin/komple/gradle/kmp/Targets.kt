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
package komple.gradle.kmp

import komple.platform.Platform
import org.jetbrains.kotlin.konan.target.KonanTarget

/**
 * Returns the [Platform] representing `this` [KonanTarget] or `null` if the target do not have a
 * [Platform] equivalent.
 */
public fun KonanTarget.toPlatform(): Platform? = when (this) {
    ANDROID_ARM32 -> Platform.androidArm32
    ANDROID_ARM64 -> Platform.androidArm64
    ANDROID_X64 -> Platform.androidX64
    ANDROID_X86 -> Platform.androidX86
    IOS_ARM64 -> Platform.iosArm64
    IOS_SIMULATOR_ARM64 -> Platform.iosSimulatorArm64
    IOS_X64 -> Platform.iosX64
    LINUX_ARM64 -> Platform.linuxArm64
    LINUX_X64 -> Platform.linuxX64
    MACOS_ARM64 -> Platform.macosArm64
    MACOS_X64 -> Platform.macosX64
    MINGW_X64 -> Platform.mingwX64
    TVOS_ARM64 -> Platform.tvosArm64
    TVOS_SIMULATOR_ARM64 -> Platform.tvosSimulatorArm64
    TVOS_X64 -> Platform.tvosX64
    WATCHOS_ARM32 -> Platform.watchosArm32
    WATCHOS_ARM64 -> Platform.watchosArm64
    WATCHOS_DEVICE_ARM64 -> Platform.watchosDeviceArm64
    WATCHOS_SIMULATOR_ARM64 -> Platform.watchosSimulatorArm64
    WATCHOS_X64 -> Platform.watchosX64
    LINUX_ARM32_HFP -> null
}