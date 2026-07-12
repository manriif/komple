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
package komple.tool

import komple.KOMPLE_PLUGIN_ID
import komple.KompleRootExtension
import komple.kompleRootExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class KompleToolPlugin : Plugin<Project> {

    /**
     * Applies the plugin only if Komple was applied.
     */
    final override fun apply(project: Project) {
        check(project.parent == null) {
            "Komple tool plugin can only be applied on root project"
        }

        project.pluginManager.withPlugin(KOMPLE_PLUGIN_ID) {
            configure(project, project.kompleRootExtension)
        }
    }

    /**
     * Applies `this` plugin on [project].
     *
     * It is guaranteed that the komple plugin was applied.
     * It is not guaranteed that the kmp plugin was applied.
     */
    protected abstract fun configure(
        project: Project,
        komple: KompleRootExtension
    )
}