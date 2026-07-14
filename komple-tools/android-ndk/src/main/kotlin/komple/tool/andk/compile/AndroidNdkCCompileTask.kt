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

import komple.project.c.CCompileTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Task compiling C library targeting Android Native.
 */
@CacheableTask
internal abstract class AndroidNdkCCompileTask :
    CCompileTask<AndroidNdkCCompileWorkAction.Parameters, AndroidNdkCCompileWorkAction>() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val toolchainDirectory: DirectoryProperty

    @get:Nested
    abstract val params: Property<AndroidNdkCompilationParams>

    override val workActionClass: KClass<AndroidNdkCCompileWorkAction>
        get() = AndroidNdkCCompileWorkAction::class

    override fun AndroidNdkCCompileWorkAction.Parameters.configure() {
        this@AndroidNdkCCompileTask.let { task ->
            this.toolchainDirectory = task.toolchainDirectory
            this.params = task.params
        }
    }
}