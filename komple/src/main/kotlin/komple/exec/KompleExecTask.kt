package komple.exec

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Base for Task requiring a [CommandExecutor].
 */
public abstract class KompleExecTask : DefaultTask() {

    @get:Inject
    internal abstract val execOperations: ExecOperations

    /**
     * Note that it is the owner responsibility to set the value.
     */
    @get:Nested
    public abstract val execEnvironment: Property<ExecEnvironment>

    /**
     * Returns a new [CommandExecutor] instance.
     */
    protected fun commandExecutor(): CommandExecutor {
        return execEnvironment.get().createCommandExecutor(execOperations)
    }
}