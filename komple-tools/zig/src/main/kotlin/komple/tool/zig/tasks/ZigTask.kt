package komple.tool.zig.tasks

import komple.tool.zig.ZigExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.assign

/**
 * Common declaration for zig tasks.
 */
internal interface ZigTask {

    /**
     * Name of the zig archive file getting downloaded.
     */
    @get:Input
    val archiveFileName: Property<String>
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Configures the [task] properties.
 */
internal fun ZigExtension.configure(task: ZigTask) {
    task.archiveFileName = archiveFileName
}