package komple.tool.task

import komple.task.enableTracking
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
    TaskRegistrationScope<Extension, ExtractTaskContext> {

    /**
     * Registers a task of type [T], [configure]s it and returns it.
     *
     * The directory where downloaded file(s) lives and where extracted file(s) must be written to
     * can be obtained from the [ExtractTaskContext.inputDirectory] and
     * [ExtractTaskContext.outputDirectory] passed to [configure]. The input directory should
     * eventually be registered as task's input. The output directory must be registered as the
     * task's only output directory.
     */
    public override fun <T : Task> register(
        klass: KClass<T>,
        cacheable: Boolean,
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
 * Registers a task of type [T], [configure]s it and returns it.
 *
 * The directory where downloaded file(s) lives and where extracted file(s) must be written to
 * can be obtained from the [ExtractTaskContext.inputDirectory] and
 * [ExtractTaskContext.outputDirectory] passed to [configure]. The input directory should
 * eventually be registered as task's input. The output directory must be registered as the
 * task's only output directory.
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
 * The [ExtractTask.execEnvironment], [ExtractTask.tracker], [ExtractTask.outputDirectory] and
 * [ExtractTask.inputDirectory] properties are configured before [configure] is invoked.
 *
 * Tracking is enabled by default.
 */
public inline fun <reified T : ExtractTask> ExtractTaskRegistrationScope<*>.extract(
    cacheable: Boolean = false,
    noinline configure: (T.(context: ExtractTaskContext) -> Unit)? = null
): TaskProvider<T> = outputTool(cacheable) { context ->
    this.inputDirectory = context.inputDirectory
    context.tracker.enableTracking()
    configure?.invoke(this, context)
}

/**
 * Registers a task of type [T] subclass of [UnarchiveExtractTask] and [configure]s it.
 *
 * The [UnarchiveExtractTask.execEnvironment], [UnarchiveExtractTask.tracker],
 * [UnarchiveExtractTask.outputDirectory], [UnarchiveExtractTask.inputDirectory] and
 * [UnarchiveExtractTask.enclosedContent] properties are configured before [configure] is invoked.
 *
 * Tracking is enabled by default.
 */
public inline fun <reified T : UnarchiveExtractTask> ExtractTaskRegistrationScope<*>.unarchive(
    enclosedContent: Boolean,
    cacheable: Boolean = false,
    noinline configure: (T.(context: ExtractTaskContext) -> Unit)? = null
): TaskProvider<T> = extract<T>(cacheable) { context ->
    this.enclosedContent = enclosedContent
    configure?.invoke(this, context)
}

/**
 * Registers a task that unzip downloaded file.
 *
 * The returned task assumes that a single file has been downloaded and that downloaded file is a
 * valid `.zip`.
 *
 * Tracking is enabled by default.
 * Note that the returned task is cacheable.
 */
public fun ExtractTaskRegistrationScope<*>.unzip(
    enclosedContent: Boolean = false,
    configure: (UnzipExtractTask.(context: ExtractTaskContext) -> Unit)? = null
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
 * Tracking is enabled by default.
 * Note that the returned task is cacheable.
 */
public fun ExtractTaskRegistrationScope<*>.untarGzip(
    enclosedContent: Boolean = false,
    configure: (UntarGzipExtractTask.(context: ExtractTaskContext) -> Unit)? = null
): TaskProvider<UntarGzipExtractTask> = unarchive(
    enclosedContent = enclosedContent,
    configure = configure
)

/**
 * Registers a task of type [T] subclass of [DmgExtractTask] and [configure]s it.
 *
 * The [DmgExtractTask.execEnvironment], [DmgExtractTask.tracker],
 * [DmgExtractTask.outputDirectory] and [DmgExtractTask.inputDirectory] properties are configured
 * before [configure] is invoked.
 *
 * Tracking is enabled by default.
 */
public inline fun <reified T : DmgExtractTask> ExtractTaskRegistrationScope<*>.dmg(
    cacheable: Boolean = false,
    noinline configure: (T.(context: ExtractTaskContext) -> Unit)? = null
): TaskProvider<T> = extract<T>(cacheable, configure)

/**
 * Registers a task extracts files from a DMG using [extractor].
 *
 * Tracking is enabled by default.
 * Note that the returned task is cacheable.
 */
public fun ExtractTaskRegistrationScope<*>.dmg(
    extractor: DmgContentExtractor,
    configure: (DefaultDmgExtractTask.(context: ExtractTaskContext) -> Unit)? = null
): TaskProvider<DefaultDmgExtractTask> = dmg { context ->
    this.extractor = extractor
    configure?.invoke(this, context)
}

/**
 * Registers a task of type [T] subclass of [CommandExtractTask] and [configure]s it.
 *
 * The [CommandExtractTask.execEnvironment], [CommandExtractTask.tracker],
 * [CommandExtractTask.outputDirectory] and [CommandExtractTask.inputDirectory] properties are
 * configured before [configure] is invoked.
 *
 * Tracking is enabled by default.
 */
public inline fun <reified T : CommandExtractTask> ExtractTaskRegistrationScope<*>.command(
    cacheable: Boolean = false,
    noinline configure: (T.(context: ExtractTaskContext) -> Unit)? = null
): TaskProvider<T> = extract<T>(cacheable, configure)

/**
 * Registers a task extracts files using a command provider by [provider].
 *
 * Tracking is enabled by default.
 * Note that the returned task is cacheable.
 */
public fun ExtractTaskRegistrationScope<*>.command(
    provider: ExtractCommandProvider
): TaskProvider<DefaultCommandExtractTask> = command(false) {
    this.provider = provider
}