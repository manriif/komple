package komple.gradle.task

import komple.task.DownloadContext
import org.gradle.api.file.Directory

/**
 * Default implementation of [DownloadContext].
 */
internal class DefaultDownloadContext(outputDirectory: Directory) :
    DownloadContext,
    DefaultTaskContext(outputDirectory)