package komple.tool.install

import komple.exec.Bash
import komple.exec.execOutput
import komple.exec.invoke
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.process.ExecOperations
import java.io.File
import kotlin.reflect.KClass

/**
 * Scope for extract task registration.
 */
public interface ExtractTaskRegistrationScope : TaskRegistrationScope {

    /**
     * Registers a task of type [T], [configure]s it and returns that registered task.
     * The task must output the extracted file(s).
     */
    public fun <T : Task> register(
        klass: KClass<T>,
        configure: T.(context: ExtractContext) -> Unit
    ): TaskProvider<T>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Registers a task of type [T] and [configure]s it.
 * The task must output the extracted file(s).
 */
public inline fun <reified T : Task> ExtractTaskRegistrationScope.register(
    noinline configure: T.(context: ExtractContext) -> Unit
): TaskProvider<T> = register(
    klass = T::class,
    configure = configure
)

/**
 * Registers a task that just forwards downloaded files, in a way that they'll be available for
 * installation, and returns that registered task.
 */
public fun ExtractTaskRegistrationScope.noOp(): TaskProvider<*> = register<DefaultTask> { context ->
    outputs.files(context.inputs.files)
}

/**
 * Registers a task that copies file from [createTree] and returns that registered task.
 *
 * If [enclosedContent] is `true` then the contents inside the root directory is moved to the root
 * and the root directory is excluded.
 */
private inline fun ExtractTaskRegistrationScope.extractFileTree(
    enclosedContent: Boolean,
    crossinline createTree: ArchiveOperations.(Provider<RegularFile>) -> FileTree
) = register<DefaultTask> { context ->
    val archiveOperations = project.serviceOf<ArchiveOperations>()
    val fileTree = archiveOperations.createTree(context.inputs.file)
    val fileOperations = project.serviceOf<FileSystemOperations>()
    val destination = context.outputDirectory
    outputs.dir(destination)

    if (enclosedContent) {
        doLast {
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

                into(destination)
            }
        }
    } else {
        doLast {
            fileOperations.copy {
                from(fileTree)
                into(destination)
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
public fun ExtractTaskRegistrationScope.unzip(enclosedContent: Boolean): TaskProvider<*> =
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
public fun ExtractTaskRegistrationScope.untarGzip(enclosedContent: Boolean): TaskProvider<*> =
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
 */
public fun ExtractTaskRegistrationScope.dmg(
    extractContent: (
        mountPoint: File,
        extractDirectory: File
    ) -> Unit
): TaskProvider<*> = register<DefaultTask> { context ->
    val execOperations = project.serviceOf<ExecOperations>()
    val extractDirectory = context.outputDirectory
    val dmgFile = context.inputs.file
    outputs.dir(extractDirectory)

    doLast {
        val dmgPath = dmgFile.get().asFile.absoluteFile

        val mountPoint = execOperations.execOutput(
            Bash("hdiutil", "attach", dmgPath, "-nobrowse", "-plist") {
                pipe("plutil", "-extract", "system-entities.0.mount-point", "raw", "-")
            }
        )

        try {
            extractContent(
                File(mountPoint),
                extractDirectory.asFile
            )
        } finally {
            execOperations.exec {
                commandLine("hdiutil", "detach", mountPoint)
            }
        }
    }
}