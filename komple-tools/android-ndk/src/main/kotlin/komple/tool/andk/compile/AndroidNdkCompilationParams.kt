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
package komple.tool.andk.compile

import komple.kompleProperty
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * Compilation parameters for Android Native compilation.
 */
public interface AndroidNdkCompilationParams {

    /**
     * Minimum Android SDK.
     */
    @get:Input
    public val minSdk: Property<String>
}

/**
 * Configures convention values for [AndroidNdkCompilationParams].
 */
internal fun AndroidNdkCompilationParams.configureConventions(project: Project) {
    minSdk.convention(project.kompleProperty("androidNdk.version.minSdk"))
}

/**
 * Configures convention values for [AndroidNdkCompilationParams].
 */
internal fun AndroidNdkCompilationParams.configureConventions(
    parent: AndroidNdkCompilationParams
) {
    minSdk.convention(parent.minSdk)
}