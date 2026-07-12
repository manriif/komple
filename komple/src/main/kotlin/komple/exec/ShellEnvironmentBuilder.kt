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
package komple.exec

import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider

/**
 * Builder for populating a [ShellEnvironment].
 */
public interface ShellEnvironmentBuilder {

    /**
     * Registers a command that will be executed first.
     */
    public fun command(commandProvider: Provider<Command>)

    /**
     * Adds [pathProvider] to the environment path variable.
     */
    public fun path(pathProvider: Provider<String>)

    /**
     * Defines [valueProvider] as environment variable for [name].
     */
    public fun variable(
        name: String,
        valueProvider: Provider<String>
    )
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Adds [pathProvider] resolved absolute path to the environment path variable.
 */
public fun ShellEnvironmentBuilder.path(pathProvider: Provider<out FileSystemLocation>) {
    path(pathProvider.map { it.asFile.absolutePath })
}

/**
 * Defines [valueProvider] resolved absolute path as environment variable for [name].
 */
public fun ShellEnvironmentBuilder.variable(
    name: String,
    valueProvider: Provider<out FileSystemLocation>
) {
    variable(
        name = name,
        valueProvider = valueProvider.map { it.asFile.absolutePath }
    )
}