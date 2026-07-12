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
package komple.tool.jext.generator

import komple.project.c.CProject
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Configuration for binding generation.
 */
public interface JextractBindingGenerator : Named {

    /**
     * The [CProject] this generator applies on.
     */
    public val cProject: CProject

    /**
     * Provider of the task responsible for binding generations.
     * The returned task output files are the generated bindings files.
     */
    public val generateTaskProvider: TaskProvider<JextractGenerateBindingsTask>

    /**
     * Directory where the files are generated.
     * This can be passed to a task input to create an implicit dependency on [generateTaskProvider].
     */
    public val generateDirectory: Provider<Directory>

    /**
     * Command line options for the jextract tool.
     */
    public val options: JextractCommandLineOptions

    /**
     * Configures the options using [configuration].
     */
    public fun options(configuration: Action<JextractCommandLineOptions>)
}