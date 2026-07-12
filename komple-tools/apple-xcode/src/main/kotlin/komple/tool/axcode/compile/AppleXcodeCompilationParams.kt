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
package komple.tool.axcode.compile

import komple.kompleProperty
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * Compilation parameters for Apple compilation.
 */
public interface AppleXcodeCompilationParams {

    /**
     * Minimum macos version.
     */
    @get:Input
    public val versionMinMacos: Property<String>

    /**
     * Minimum ios version.
     */
    @get:Input
    public val versionMinIos: Property<String>

    /**
     * Minimum tvos version.
     */
    @get:Input
    public val versionMinTvos: Property<String>

    /**
     * Minimum watchos SDK.
     */
    @get:Input
    public val versionMinWatchos: Property<String>
}

/**
 * Configures convention values for [AppleXcodeCompilationParams].
 */
internal fun AppleXcodeCompilationParams.configureConventions(project: Project) {
    versionMinMacos.convention(project.kompleProperty("appleXcode.version.min.macos"))
    versionMinIos.convention(project.kompleProperty("appleXcode.version.min.ios"))
    versionMinTvos.convention(project.kompleProperty("appleXcode.version.min.tvos"))
    versionMinWatchos.convention(project.kompleProperty("appleXcode.version.min.watchos"))
}

/**
 * Configures convention values for [AppleXcodeCompilationParams].
 */
internal fun AppleXcodeCompilationParams.configureConventions(parent: AppleXcodeCompilationParams) {
    versionMinMacos.convention(parent.versionMinMacos)
    versionMinIos.convention(parent.versionMinIos)
    versionMinTvos.convention(parent.versionMinTvos)
    versionMinWatchos.convention(parent.versionMinWatchos)
}