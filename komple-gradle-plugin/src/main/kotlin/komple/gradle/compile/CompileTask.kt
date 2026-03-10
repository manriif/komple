package komple.gradle.compile

import komple.KOMPLE_EXEC_SERVICE_NAME
import komple.exec.ExecService
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.CacheableTask

@CacheableTask
public abstract class CompileTask : DefaultTask() {

    @get:ServiceReference(KOMPLE_EXEC_SERVICE_NAME)
    internal abstract val execService: Property<ExecService>
}