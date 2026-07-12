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
package komple.tool.task

import komple.exec.HasExecEnvironment
import komple.platform.HasHost
import komple.task.TaskStateTracker
import org.gradle.api.file.Directory

/**
 * Context for tool task.
 */
public interface ToolTaskContext : HasExecEnvironment, HasHost

/**
 * Context for tool task that is expected to produce output files.
 */
public interface OutputToolTaskContext : ToolTaskContext {

    /**
     * State tracker for the task.
     */
    public val tracker: TaskStateTracker

    /**
     * The directory that must be used to write task produced file(s).
     * The task output directory is used as input directory for the next task.
     */
    public val outputDirectory: Directory
}