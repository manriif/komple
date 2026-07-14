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

import komple.task.integrity.ChecksumIntegrityTask
import komple.task.integrity.DigestAlgorithm
import komple.task.integrity.IntegrityTask
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Scope for integrity task registration.
 */
public interface IntegrityTaskRegistrationScope<Extension : KompleToolExtension> :
    TaskRegistrationScope<Extension, IntegrityTaskContext> {

    /**
     * Registers a task of type [T], [configure]s it and returns it.
     * 
     * Downloaded file(s) lives in the [IntegrityTaskContext.inputDirectory] passed to [configure].
     * The task should only perform integrity checking against downloaded file(s). That directory
     * should eventually be registered as task's input.
     *
     * The task must not register any output.
     */
    public override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
        configure: T.(context: IntegrityTaskContext) -> Unit
    ): TaskProvider<T>

    /**
     * Returns a task that skips integrity checking, causing downloaded files to be considered safe.
     *
     * Note that skipping integrity check is discouraged if the tools is getting downloaded from an
     * external source. It is however possible to perform the check in the download task directly.
     */
    override fun skip(): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T], [configure]s it and returns it.
 *
 * Downloaded file(s) lives in the [IntegrityTaskContext.inputDirectory] passed to [configure].
 * The task should only perform integrity checking against downloaded file(s). That directory
 * should eventually be registered as task's input.
 *
 * The task must not register any output.
 */
public inline fun <reified T : Task> IntegrityTaskRegistrationScope<*>.register(
    cacheable: Boolean = false,
    noinline configure: T.(context: IntegrityTaskContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    cacheable = cacheable,
    configure = configure
)

/**
 * Registers a task of type [T] subclass of [IntegrityTask] and [configure]s it.
 *
 * The [IntegrityTask.execEnvironment] and [IntegrityTask.inputDirectory] properties are configured
 * before [configure] is invoked.
 */
public inline fun <reified T : IntegrityTask> IntegrityTaskRegistrationScope<*>.integrity(
    cacheable: Boolean = false,
    noinline configure: (T.(context: IntegrityTaskContext) -> Unit)? = null
): TaskProvider<T> = tool(cacheable) { context ->
    this.inputDirectory = context.inputDirectory
    configure?.invoke(this, context)
}

/**
 * Registers an integrity task that validate the file integrity against [checksum] using
 * [algorithm].
 *
 * It is assumed that only a single file has been downloaded.
 * Note that the returned task is cacheable.
 */
public fun IntegrityTaskRegistrationScope<*>.checksum(
    checksum: Provider<String>,
    algorithm: DigestAlgorithm,
    configure: (ChecksumIntegrityTask.(context: IntegrityTaskContext) -> Unit)? = null
): TaskProvider<ChecksumIntegrityTask> = integrity { context ->
    this.checksum = checksum
    this.algorithm = algorithm
    configure?.invoke(this, context)
}

/**
 * Registers an integrity task that validate the file integrity against [checksum] using
 * [algorithm].
 *
 * It is assumed that only a single file has been downloaded.
 * Note that the returned task is cacheable.
 */
public fun IntegrityTaskRegistrationScope<*>.checksum(
    checksum: String,
    algorithm: DigestAlgorithm,
    configure: (ChecksumIntegrityTask.(context: IntegrityTaskContext) -> Unit)? = null
): TaskProvider<ChecksumIntegrityTask> = checksum(
    checksum = providers.provider { checksum },
    algorithm = algorithm,
    configure = configure
)