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
package komple.gradle

import komple.KOMPLE_EXTENSION_NAME
import komple.KOMPLE_PLUGIN_ID
import komple.gradle.extension.KompleRootProjectExtension
import komple.gradle.extension.KompleSubProjectExtension
import komple.gradle.extension.configureConventions
import komple.gradle.extension.configureSubProjectExtension
import komple.gradle.project.registerProjectFactories
import komple.gradle.tool.configureTools
import komple.loadKompleProperties
import komple.util.getExtensionByName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

/**
 * Komple Gradle plugin.
 */
public class KomplePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (project.rootProject == project) {
            applyRootProjectExtension(project)
        } else {
            applySubProjectExtension(project)
        }
    }

    /**
     * Applies the root project extension which is meant for Komple tools and projects declaration.
     */
    private fun applyRootProjectExtension(project: Project) {
        val extension = project.extensions.create<KompleRootProjectExtension>(KOMPLE_EXTENSION_NAME)

        extension.run {
            configureConventions()
            project.registerProjectFactories(extensibleProjects, projectConfiguratorFactories)
        }

        project.run {
            loadKompleProperties(KomplePlugin::class.java.classLoader)
            configureTools(extension)
        }
    }

    /**
     * Applies the subproject extension which is meant for Komple registered tools and projects
     * consumption.
     */
    private fun applySubProjectExtension(project: Project) {
        val rootProject = project.rootProject

        rootProject.pluginManager.withPlugin(KOMPLE_PLUGIN_ID) {
            val extension =
                project.extensions.create<KompleSubProjectExtension>(KOMPLE_EXTENSION_NAME)

            val rootExtension =
                rootProject.getExtensionByName<KompleRootProjectExtension>(KOMPLE_EXTENSION_NAME)

            project.configureSubProjectExtension(extension, rootExtension)
        }
    }
}