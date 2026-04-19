package komple.exec

import komple.KOMPLE_EXEC_SERVICE_NAME
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference

/**
 * Base for Task requiring Komple [ExecService].
 */
public abstract class KompleExecTask : DefaultTask() {

    @get:ServiceReference(KOMPLE_EXEC_SERVICE_NAME)
    protected abstract val execService: Property<ExecService>
}