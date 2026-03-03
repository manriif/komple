package komple.compile

import org.gradle.api.Named
import org.gradle.api.file.ConfigurableFileCollection

public interface KompleCompilation: Named {

    public val sourceFiles: ConfigurableFileCollection
}