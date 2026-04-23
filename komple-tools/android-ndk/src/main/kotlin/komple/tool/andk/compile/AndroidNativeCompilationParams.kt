package komple.tool.andk.compile

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

internal const val DEFAULT_ANDROID_MIN_SDK_VERSION = "21"

/**
 * Compilation parameters for Android Native compilation.
 */
public interface AndroidNativeCompilationParams {

    /**
     * Minimum Android SDK version.
     * Default to 21.
     */
    @get:Input
    public val minSdk: Property<String>
}

/**
 * Configure convention values for [AndroidNativeCompilationParams].
 */
internal fun AndroidNativeCompilationParams.configureConventions() {
    minSdk.convention(DEFAULT_ANDROID_MIN_SDK_VERSION)
}