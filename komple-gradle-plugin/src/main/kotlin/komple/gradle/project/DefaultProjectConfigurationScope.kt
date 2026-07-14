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
package komple.gradle.project

import komple.exec.ExecEnvironment
import komple.gradle.extension.DefaultExtensionScope
import komple.gradle.platform.CurrentHost
import komple.gradle.tool.KompleToolConfigContext
import komple.gradle.util.ClosableScope
import komple.gradle.util.dashCased
import komple.gradle.util.pascalCased
import komple.platform.Host
import komple.project.ProjectConfigurationScope
import komple.project.ProjectConfigurator
import komple.task.TaskStateTracker
import komple.tool.extension.ExtensionScope
import komple.tool.extension.HasExtension
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import kotlin.reflect.KClass

/**
 * Default implementation of [ProjectConfigurationScope].
 */
internal class DefaultProjectConfigurationScope<Extension : KompleToolExtension>(
    private val context: KompleToolConfigContext<Extension>,
    private val projectExtension: KompleProjectExtension,
    override val configurator: ProjectConfigurator,
    override val installDirectory: Provider<Directory>,
) : ProjectConfigurationScope<Extension>,
    HasExtension<Extension> by context,
    ClosableScope() {

    override val host: Host
        get() = notClosed { CurrentHost }

    override val execEnvironment: ExecEnvironment
        get() = context.execEnvironmentProvider.get()

    override fun generatedDirectory(): Provider<Directory> {
        return context.project.layout.projectGeneratedOutputDir(
            projectName = configurator.project.name,
            subdirectory = context.toolName.dashCased()
        )
    }

    override fun <E : Any> createExtension(
        name: String,
        type: KClass<E>,
        vararg args: Any,
        configure: (ExtensionScope<E>.() -> Unit)?
    ): E = notClosed {
        projectExtension.extensions.create(
            name = name,
            type = type,
            constructionArguments = args
        ).also { extension ->
            DefaultExtensionScope(context.project, extension, configure)
        }
    }

    override fun <T : Task> registerTask(
        postfix: String,
        type: KClass<T>,
        cacheable: Boolean,
        configure: (T.(TaskStateTracker) -> Unit)?
    ): TaskProvider<T> {
        val name = projectDerivedName(
            projectName = configurator.project.name,
            postfix = "${context.toolName}${postfix.pascalCased()}"
        )

        return context.project.tasks.registerProjectTask(name, type, cacheable) { context ->
            configure?.invoke(this, context)
        }
    }
}