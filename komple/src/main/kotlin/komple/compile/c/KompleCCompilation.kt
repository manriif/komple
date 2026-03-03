package komple.compile.c

import komple.compile.KompleCompilation
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty

public abstract class KompleCCompilation : KompleCompilation {

    public abstract val headerFile: RegularFileProperty

    public abstract val headerFilters: ConfigurableFileCollection

    public abstract val includeDirs: ConfigurableFileCollection

    public abstract val defines: MapProperty<String, String>
}