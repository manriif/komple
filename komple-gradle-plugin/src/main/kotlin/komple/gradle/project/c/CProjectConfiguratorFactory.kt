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
package komple.gradle.project.c

import komple.gradle.project.ProjectConfiguratorFactory
import komple.gradle.tool.DefaultKompleTool
import komple.project.ProjectConfigurator
import kotlin.reflect.KClass

/**
 * Implementation of [ProjectConfiguratorFactory] for [DefaultCProject].
 */
internal class CProjectConfiguratorFactory(override val kProject: DefaultCProject) :
    ProjectConfiguratorFactory<CProjectExtension> {

    override val extensionType: KClass<out CProjectExtension>
        get() = CProjectExtension::class

    override fun createConfigurator(
        extension: CProjectExtension,
        tool: DefaultKompleTool<*>
    ): ProjectConfigurator = DefaultCProjectConfigurator(
        extension = extension,
        execEnvironmentProvider = tool.usageExecEnvironmentProvider
    )
}