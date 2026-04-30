package komple.tool.axcode.compile

import komple.kompleProperty
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * Compilation parameters for Apple compilation.
 */
public interface AppleXcodeCompilationParams {

    /**
     * Minimum macos version.
     */
    @get:Input
    public val versionMinMacos: Property<String>

    /**
     * Minimum ios version.
     */
    @get:Input
    public val versionMinIos: Property<String>

    /**
     * Minimum tvos version.
     */
    @get:Input
    public val versionMinTvos: Property<String>

    /**
     * Minimum watchos SDK.
     */
    @get:Input
    public val versionMinWatchos: Property<String>
}

/**
 * Configures convention values for [AppleXcodeCompilationParams].
 */
internal fun AppleXcodeCompilationParams.configureConventions(project: Project) {
    versionMinMacos.convention(project.kompleProperty("appleXcode.version.min.macos"))
    versionMinIos.convention(project.kompleProperty("appleXcode.version.min.ios"))
    versionMinTvos.convention(project.kompleProperty("appleXcode.version.min.tvos"))
    versionMinWatchos.convention(project.kompleProperty("appleXcode.version.min.watchos"))
}

/**
 * Configures convention values for [AppleXcodeCompilationParams].
 */
internal fun AppleXcodeCompilationParams.configureConventions(parent: AppleXcodeCompilationParams) {
    versionMinMacos.convention(parent.versionMinMacos)
    versionMinIos.convention(parent.versionMinIos)
    versionMinTvos.convention(parent.versionMinTvos)
    versionMinWatchos.convention(parent.versionMinWatchos)
}