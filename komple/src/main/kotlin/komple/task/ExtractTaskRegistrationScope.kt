package komple.task

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.serviceOf
import kotlin.reflect.KClass

/**
 * Scope for extract task registration.
 */
public interface ExtractTaskRegistrationScope : TaskRegistrationScope {

    /**
     * Registers a task of type [T], [configure]s it and returns it.
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
 * installation, and returns it.
 */
public fun ExtractTaskRegistrationScope.skip(): TaskProvider<*> = register<DefaultTask> { context ->
    outputs.files(context.inputs.files)
}

/**
 * Registers a task that copies file from [createTree] and returns it.
 *
 * If [enclosedContent] is `true` then the contents inside the root directory is moved to the root
 * and the root directory is excluded.
 */
private inline fun ExtractTaskRegistrationScope.extractFileTree(
    enclosedContent: Boolean,
    crossinline createTree: Project.(Provider<RegularFile>) -> FileTree
) = register<DefaultTask> { context ->
    val sources = project.createTree(context.inputs.file)
    val fileOperations = project.serviceOf<FileSystemOperations>()
    val destination = context.outputDirectory

    if (enclosedContent) {
        doLast {
            fileOperations.copy {
                from(sources) {
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
                from(sources)
                into(destination)
            }
        }
    }
}

/**
 * Registers a task that unzip downloaded file and returns it.
 *
 * If [enclosedContent] is `true` then the contents inside the root directory is moved to the root
 * and the root directory is excluded.
 *
 * It is assumed that a single file has been downloaded and that downloaded file is a valid `zip`.
 */
public fun ExtractTaskRegistrationScope.unzip(enclosedContent: Boolean): TaskProvider<*> =
    extractFileTree(enclosedContent) { zipTree(it) }

/**
 * Registers a task that untar downloaded file and returns it.
 *
 * If [enclosedContent] is `true` then the contents inside the root directory is moved to the root
 * and the root directory is excluded.
 *
 * It is assumed that a single file has been downloaded and that downloaded file is a valid
 * `tar.gz`.
 */
public fun ExtractTaskRegistrationScope.untarGzip(enclosedContent: Boolean): TaskProvider<*> =
    extractFileTree(enclosedContent) { tarTree(resources.gzip(it)) }