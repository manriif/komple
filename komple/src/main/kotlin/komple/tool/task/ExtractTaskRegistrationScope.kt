package komple.tool.task

import komple.task.extract.CommandExtractTask
import komple.task.extract.DefaultCommandExtractTask
import komple.task.extract.DefaultDmgExtractTask
import komple.task.extract.DmgContentExtractor
import komple.task.extract.DmgExtractTask
import komple.task.extract.ExtractCommandProvider
import komple.task.extract.ExtractTask
import komple.task.extract.UnarchiveExtractTask
import komple.task.extract.UntarGzipExtractTask
import komple.task.extract.UnzipExtractTask
import komple.tool.extension.KompleToolExtension
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.assign
import kotlin.reflect.KClass

/**
 * Scope for extract task registration.
 */
public interface ExtractTaskRegistrationScope<Extension : KompleToolExtension> :
    TaskRegistrationScope<Extension> {

    /**
     * Registers a task of type [T], [configure]s it and returns that registered task.
     *
     * The directory where downloaded file(s) lives and where extracted file(s) must be written to
     * can be obtained from the [ExtractTaskContext] passed to [configure].
     *
     * The task must not register any output and instead use the context directory to put all
     * its files.
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean = false,
        configure: T.(context: ExtractTaskContext) -> Unit
    ): TaskProvider<T>

    /**
     * Registers a task that skips extraction, causing downloaded files to be used as extracted
     * files.
     */
    override fun skip(): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T] and [configure]s it.
 *
 * The directory where downloaded file(s) lives and where extracted file(s) must be written to
 * can be obtained from the [ExtractTaskContext] passed to [configure].
 */
public inline fun <reified T : Task> ExtractTaskRegistrationScope<*>.register(
    cacheable: Boolean = false,
    noinline configure: T.(context: ExtractTaskContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    cacheable = cacheable,
    configure = configure
)

/**
 * Registers a task of type [T] subclass of [ExtractTask] and [configure]s it.
 *
 * The [ExtractTask.context] is configured before [configure] is invoked.
 */
public inline fun <reified T : ExtractTask> ExtractTaskRegistrationScope<*>.extract(
    cacheable: Boolean = false,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = register<T>(cacheable) { context ->
    this.context = context
    configure?.invoke(this)
}

/**
 * Registers a task of type [T] subclass of [UnarchiveExtractTask] and [configure]s it.
 *
 * The [UnarchiveExtractTask.context] and [UnarchiveExtractTask.enclosedContent] properties are
 * configured before [configure] is invoked.
 */
public inline fun <reified T : UnarchiveExtractTask> ExtractTaskRegistrationScope<*>.unarchive(
    enclosedContent: Boolean,
    cacheable: Boolean = false,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = extract<T>(cacheable) {
    this.enclosedContent = enclosedContent
    configure?.invoke(this)
}

/**
 * Registers a task that unzip downloaded file.
 *
 * The returned task assumes that a single file has been downloaded and that downloaded file is a
 * valid `.zip`.
 *
 * Note that the returned task is cacheable.
 */
public fun ExtractTaskRegistrationScope<*>.unzip(
    enclosedContent: Boolean = false,
    configure: (UnzipExtractTask.() -> Unit)? = null
): TaskProvider<UnzipExtractTask> = unarchive(
    enclosedContent = enclosedContent,
    configure = configure
)

/**
 * Registers a task that untar downloaded file.
 *
 * The returned task assumes that a single file has been downloaded and that downloaded file is a
 * valid `.tar.gz`.
 *
 * Note that the returned task is cacheable.
 */
public fun ExtractTaskRegistrationScope<*>.untarGzip(
    enclosedContent: Boolean = false,
    configure: (UntarGzipExtractTask.() -> Unit)? = null
): TaskProvider<UntarGzipExtractTask> = unarchive(
    enclosedContent = enclosedContent,
    configure = configure
)

/**
 * Registers a task of type [T] subclass of [DmgExtractTask] and [configure]s it.
 *
 * The [DmgExtractTask.context] property is configured before [configure] is invoked.
 */
public inline fun <reified T : DmgExtractTask> ExtractTaskRegistrationScope<*>.dmg(
    cacheable: Boolean = false,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = extract<T>(cacheable, configure)

/**
 * Registers a task extracts files from a DMG using [extractor].
 *
 * Note that the returned task is cacheable.
 */
public fun ExtractTaskRegistrationScope<*>.dmg(
    extractor: DmgContentExtractor,
    configure: (DefaultDmgExtractTask.() -> Unit)? = null
): TaskProvider<DefaultDmgExtractTask> = dmg {
    this.extractor = extractor
    configure?.invoke(this)
}

/**
 * Registers a task of type [T] subclass of [CommandExtractTask] and [configure]s it.
 *
 * The [CommandExtractTask.context] property is configured before [configure] is invoked.
 */
public inline fun <reified T : CommandExtractTask> ExtractTaskRegistrationScope<*>.command(
    cacheable: Boolean = false,
    noinline configure: (T.() -> Unit)? = null
): TaskProvider<T> = extract<T>(cacheable, configure)

/**
 * Registers a task extracts files using a command provider by [provider].
 *
 * Note that the returned task is cacheable.
 */
public fun ExtractTaskRegistrationScope<*>.command(
    provider: ExtractCommandProvider
): TaskProvider<DefaultCommandExtractTask> = command(false) {
    this.provider = provider
}