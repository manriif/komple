# Module Komple Tool Android NDK

Downloads and installs the [Android NDK](https://developer.android.com/ndk) and registers it as a
C compiler for the `Android` operating system on C projects.

## Supported hosts

macOS (any architecture), Linux (X64) and Windows (X64) — Linux and Windows on Arm64 are not
supported.

## Extension

Applying the plugin registers an `androidNdk` tool exposing the following DSL:

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.androidNdk)
}

komple {
    androidNdk {
        version = "<ndk_version>"

        checksums {
            macos = "<macos_checksum>"
            linux = "<linux_checksum>"
            windows = "<windows_checksum>"
        }

        compilationParams {
            minSdk = "24"
        }
    }
}
```

## Environment

Installation does not put anything on `PATH`; it exposes `ANDROID_NDK_HOME` and `ANDROID_NDK_ROOT`,
both pointing at the install directory, for other tools or tasks that expect them.