# Module Komple Tool Apple Xcode

Registers the locally installed Xcode toolchain as a C compiler for Darwin platforms
(macOS, iOS, tvOS, watchOS) on C projects. Unlike other Komple tools, it does not
download or install anything: it drives `xcrun`/`clang` from whatever Xcode or Command Line Tools
are already present on the machine. Hence, all the SDKs for the platform a CProject should compile 
to must be installed on the host.  

## Supported hosts

macOS only.

## Extension

Applying the plugin registers an `appleXcode` tool exposing the below DSL:

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.appleXcode)
}

komple {
    appleXcode {
        compilationParams {
            versionMinMacos = "<min_macos_version>"
            versionMinIos = "<min_ios_version>"
            versionMinTvos = "<min_tvos_version>"
            versionMinWatchos = "<min_watchos_version>"
        }
    }
}
```