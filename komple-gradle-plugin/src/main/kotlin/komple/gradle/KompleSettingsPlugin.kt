package komple.gradle

import komple.KOMPLE_PLUGIN_ID
import komple.KOMPLE_ROOT_PLUGIN_ID
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * Komple plugin that applies to settings script.
 */
public class KompleSettingsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.dependencyResolutionManagement {
            versionCatalogs {
                create("kompleTools") {
                    val kompleVersion = extractKompleVersion()
                    version(KOMPLE_ALIAS, kompleVersion)

                    plugin("komple-root", KOMPLE_ROOT_PLUGIN_ID).versionRef(KOMPLE_ALIAS)
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