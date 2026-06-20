package komple.tool.zig.compile

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * Compilation parameters for zig cross-compilation.
 */
public interface ZigCompilationParams {

    /**
     * Minimum Windows version.
     */
    @get:Input
    @get:Optional
    public val linuxVersionMin: Property<String>

    /**
     * Minimum Windows version.
     */
    @get:Input
    @get:Optional
    public val windowsVersionMin: Property<String>
}