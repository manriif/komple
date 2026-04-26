package komple.exec

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * Base for Task requiring Komple [CommandExecutor].
 */
public abstract class KompleExecTask : DefaultTask() {

    /**
     * Note that it is the owner responsibility to set the value.
     */
    @get:Nested
    public abstract val commandExecutor: Property<CommandExecutor>
}