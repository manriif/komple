package komple.tool.task

import komple.exec.Bash
import komple.exec.execOutput
import komple.exec.invoke
import komple.tool.extension.KompleToolExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskInputs
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.process.ExecOperations
import java.io.File
import kotlin.reflect.KClass

/**
 * Scope for extract task registration.
 */
public interface ExtractTaskRegistrationScope<Extension : KompleToolExtension> :
    TaskRegistrationScope<Extension> {

    /**
     * Registers a task of type [T], [configure]s it and returns that registered task.
     * The task must output the extracted file(s).
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: ExtractTaskContext) -> Unit
    ): TaskProvider<T>

    /**
     * Registers a task that skips extraction, causing downloaded files to be used as extracted
     * files.
     */
    public fun skipExtraction(): TaskProvider<*>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T] and [configure]s it.
 * The task must output the extracted file(s).
 */
public inline fun <reified T : Task> ExtractTaskRegistrationScope<*>.register(
    noinline configure: T.(context: ExtractTaskContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    configure = configure
)

/**
 * Registers a task that copies file from [createTree] and returns that registered task.
 *
 * If [enclosedContent] is `true` then the contents inside the root directory is moved to the root
 * and the root directory is excluded.
 */
private inline fun ExtractTaskRegistrationScope<*>.extractFileTree(
    enclosedContent: Boolean,
    crossinline createTree: ArchiveOperations.(Provider<RegularFile>) -> FileTree
) = register<DefaultTask> { context ->
    val archiveOperations = project.serviceOf<ArchiveOperations>()
    val fileTree = archiveOperations.createTree(context.inputs.file)
    val fileOperations = project.serviceOf<FileSystemOperations>()
    outputs.dir(context.outputDirectory)

    if (enclosedContent) {
        context.doLastWhenOutputChanged {
            fileOperations.copy {
                from(fileTree) {
                    eachFile {
                        val segments = relativePath.segments

                        if (segments.size > 1) {
                            relativePath = RelativePath(
                                true,
                                *segments.drop(1).toTypedArray()
                            )
                        } else {
                            exclude()
                        }
                    }
                }

                into(context.outputDirectory)
            }
        }
    } else {
        context.doLastWhenOutputChanged {
            fileOperations.copy {
                from(fileTree)
                into(context.outputDirectory)
            }
        }
    }
}

/**
 * Registers a task that unzip downloaded file.
 *
 * If [enclosedContent] is `true` then the contents inside the root directory is moved to the root
 * and the root directory is excluded.
 *
 * It is assumed that a single file has been downloaded and that downloaded file is a valid `.zip`.
 */
public fun ExtractTaskRegistrationScope<*>.unzip(enclosedContent: Boolean = false): TaskProvider<*> =
    extractFileTree(enclosedContent) { zipTree(it) }

/**
 * Registers a task that untar downloaded file.
 *
 * If [enclosedContent] is `true` then the contents inside the root directory is moved to the root
 * and the root directory is excluded.
 *
 * It is assumed that a single file has been downloaded and that downloaded file is a valid
 * `.tar.gz`.
 */
public fun ExtractTaskRegistrationScope<*>.untarGzip(enclosedContent: Boolean = false): TaskProvider<*> =
    extractFileTree(enclosedContent) { tarTree(gzip(it)) }

/**
 * Registers a task extracts files from a DMG with the below steps:
 *
 * 1. Mount the DMG
 * 2. Invokes [extractContent] with the path to the mounted image.
 * 3. Unmount the DMG
 *
 * The first argument to [extractContent] is a [File] pointing to the mount-point.
 * The second argument to [extractContent] is the directory where the extracted files must be
 * written.
 *
 * It is assumed that a single file has been downloaded and that downloaded file is a valid `.dmg`.
 *
 * Additional task inputs can be registered by supplying a [configureInputs].
 */
public fun ExtractTaskRegistrationScope<*>.dmg(
    configureInputs: (TaskInputs.(context: ExtractTaskContext) -> Unit)? = null,
    extractContent: FileSystemOperations.(
        mountPoint: File,
        extractDirectory: File
    ) -> Unit
): TaskProvider<*> = register<DefaultTask> { context ->
    val execOperations = project.serviceOf<ExecOperations>()
    val fileOperations = project.serviceOf<FileSystemOperations>()
    val dmgFile = context.inputs.file

    configureInputs?.invoke(inputs, context)
    outputs.dir(context.outputDirectory)

    context.doLastWhenOutputChanged {
        val dmgPath = dmgFile.get().asFile.absoluteFile

        val mountPoint = execOperations.execOutput(
            Bash("hdiutil", "attach", dmgPath, "-nobrowse", "-plist") {
                pipe(
                    "xpath",
                    "-e",
                    """'//key[.="mount-point"]/following-sibling::string[1]/text()'""",
                    "2>/dev/null"
                )

                pipe("tail", "-1")
            }
        )

        try {
            fileOperations.extractContent(
                File(mountPoint),
                context.outputDirectory.asFile
            )
        } finally {
            execOperations.exec {
                commandLine("hdiutil", "detach", mountPoint)
            }
        }
    }
}