package komple.gradle

import org.gradle.api.Project

/**
 * Represents a Komple entity associated with a Gradle [Project].
 */
public interface HasProject {

    /**
     * The Gradle [Project] associated with the entity.
     */
    public val project: Project
}
