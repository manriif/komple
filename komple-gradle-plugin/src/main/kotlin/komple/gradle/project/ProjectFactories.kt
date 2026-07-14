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

import komple.gradle.project.c.CProjectConfiguratorFactory
import komple.gradle.project.c.DefaultCProject
import komple.gradle.project.c.configureConventions
import komple.project.KompleProject
import komple.project.c.CProject
import org.gradle.api.DomainObjectSet
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance

/**
 * Registers factories for supported [DefaultKompleProject] types.
 */
internal fun Project.registerProjectFactories(
    projects: ExtensiblePolymorphicDomainObjectContainer<KompleProject>,
    factories: DomainObjectSet<ProjectConfiguratorFactory<*>>
) {
    registerProjectFactory<CProject, DefaultCProject>(
        projects = projects,
        factories = factories,
        configureConventions = DefaultCProject::configureConventions,
        createFactory = ::CProjectConfiguratorFactory
    )

    // Force all project creation for subproject DSL
    projects.all {}
}

/**
 * Registers a factory for a [KompleProject] of public type [P] and implementation type [I].
 */
internal inline fun <reified P : KompleProject, reified I : P> Project.registerProjectFactory(
    projects: ExtensiblePolymorphicDomainObjectContainer<KompleProject>,
    factories: DomainObjectSet<ProjectConfiguratorFactory<*>>,
    crossinline configureConventions: (I, Project) -> Unit,
    crossinline createFactory: (I) -> ProjectConfiguratorFactory<*>,
    vararg args: Any
) {
    projects.registerFactory(P::class.java) { name ->
        objects.newInstance<I>(name, *args).apply {
            configureCommonConventions(this@registerProjectFactory)
            configureConventions(this, this@registerProjectFactory)
            factories.add(createFactory(this))
        }
    }
}