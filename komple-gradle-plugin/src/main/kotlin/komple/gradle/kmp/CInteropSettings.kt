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

import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskProvider

/**
 * Configuration settings for Kotlin/Native [cinterop tool](https://kotlinlang.org/docs/native-c-interop.html).
 */
public interface CInteropSettings {

    /**
     * Provider of the task responsible to generate the interop def file.
     */
    public val generateDefFileTaskProvider: TaskProvider<GenerateCInteropDefTask>

    /**
     * Include functions of the given names in the generated bindings.
     */
    public val excludedFunctions: ListProperty<String>

    /**
     * Include functions of the given names in the generated bindings.
     */
    public val noStringConversion: ListProperty<String>

    /**
     * Adds additional options that are passed to the cinterop tool.
     */
    public fun extraOpts(vararg values: Any)

    /**
     * Adds additional options that are passed to the cinterop tool.
     */
    public fun extraOpts(values: List<Any>)
}

