package komple.gradle.tool

import komple.exec.HasExecEnvironment
import komple.tool.KompleTool

/**
 * Komple tool with extended properties available in root Gradle project.
 */
public interface RootKompleTool : KompleTool, HasExecEnvironment {

    /**
     * Makes this tool depends on [other].
     *
     * This implies that:
     * - [other] is installed when `this` tool is required.
     * - [other] contributes to the environment of `this` tool.
     */
    public infix fun dependsOn(other: RootKompleTool)
}