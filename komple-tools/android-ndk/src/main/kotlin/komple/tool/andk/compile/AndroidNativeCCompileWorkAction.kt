package komple.tool.andk.compile

import komple.project.c.CCompileWorkAction
import org.gradle.api.provider.Property

internal abstract class AndroidNativeCCompileWorkAction:
    CCompileWorkAction<AndroidNativeCCompileWorkAction.Parameters> {

    override fun execute() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////

    internal interface Parameters: CCompileWorkAction.Parameters {

        val params: Property<AndroidNativeCompilationParams>
    }
}