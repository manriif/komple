package komple.gradle.tool.install

import komple.tool.install.DownloadContext
import org.gradle.api.file.Directory

/**
 * Default implementation of [DownloadContext].
 */
internal class DefaultDownloadContext(outputDirectory: Directory) :
    DownloadContext,
    DefaultTaskContext(outputDirectory)