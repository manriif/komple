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
package komple.project

import komple.platform.Platform
import komple.project.c.CCompileTask
import komple.project.c.CProject
import kotlin.reflect.KClass

/**
 * [KompleProject] configurator.
 */
public sealed interface ProjectConfigurator {

    /**
     * The [KompleProject] to configure.
     */
    public val project: KompleProject
}

///////////////////////////////////////////////////////////////////////////
// C
///////////////////////////////////////////////////////////////////////////

/**
 * Configurator for [komple.project.c.CProject].
 */
public interface CProjectConfigurator : ProjectConfigurator {

    override val project: CProject

    /**
     * Register a [CCompileTask] that is created when a compilation is created and configured with
     * [configure].
     */
    public fun <Task : CCompileTask<*, *>> registerCompileTask(
        klass: KClass<out Task>,
        configure: (Task.() -> Unit)? = null,
        platformFilter: (Platform) -> Boolean
    )
}

/**
 * Register a [CCompileTask] that is created when a compilation is created and configured with
 * [configure].
 */
public inline fun <reified Task : CCompileTask<*, *>> CProjectConfigurator.registerCompileTask(
    noinline configure: (Task.() -> Unit)? = null,
    noinline platformFilter: (Platform) -> Boolean
) {
    registerCompileTask(Task::class, configure, platformFilter)
}