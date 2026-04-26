package komple.tool.andk.compile

import komple.kompleProperty
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * Compilation parameters for Android Native compilation.
 */
public interface AndroidNdkCompilationParams {

    /**
     * Minimum Android SDK.
     */
    @get:Input
    public val minSdk: Property<String>
}

/**
 * Configures convention values for [AndroidNdkCompilationParams].
 */
internal fun AndroidNdkCompilationParams.configureConventions(project: Project) {
    minSdk.convention(project.kompleProperty("androidNdk.version.minSdk"))
}

/**
 * Configures convention values for [AndroidNdkCompilationParams].
 */
internal fun AndroidNdkCompilationParams.configureConventions(
    parent: AndroidNdkCompilationParams
) {
    minSdk.convention(parent.minSdk)
}