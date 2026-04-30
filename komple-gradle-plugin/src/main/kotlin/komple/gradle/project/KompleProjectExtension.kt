package komple.gradle.project

import komple.project.KompleProject
import org.gradle.api.plugins.ExtensionAware

/**
 * Extension for a configured project.
 */
public interface KompleProjectExtension : ExtensionAware {

    /**
     * Target project.
     */
    public val kProject: KompleProject
}