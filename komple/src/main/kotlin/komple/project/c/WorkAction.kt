package komple.project.c

import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters

/**
 * Work action for C compilation.
 */
public interface CCompileWorkAction<Params: CCompileWorkAction.Parameters> : WorkAction<Params> {

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    public interface Parameters : WorkParameters {

        public val cProject: Property<CProject>
        public val compilation: Property<CCompilation>
    }
}