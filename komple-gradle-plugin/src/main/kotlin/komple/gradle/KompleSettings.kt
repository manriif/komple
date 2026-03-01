package komple.gradle

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class KompleSettings : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.dependencyResolutionManagement {
            versionCatalogs {
                create("kompleTools") {
                    val kompleVersion = extractKompleVersion()

                    version(KOMPLE_ALIAS, kompleVersion)
                    plugin("komple", KOMPLE_PLUGIN_ID).versionRef(KOMPLE_ALIAS)

                    listOf(
                        "android-ndk"
                    ).forEach { lib ->
                        library(lib, KOMPLE_PLUGIN_ID, "komple-$lib").versionRef(KOMPLE_ALIAS)
                    }
                }
            }
        }
    }
}