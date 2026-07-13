# Module Komple Tool CMake

Downloads and installs [CMake](https://cmake.org/) and exposes its `bin` directory on the shell
`PATH` of every tool or task that depends on it. It is a plain shell-environment tool, typically 
depended on by other tools that need to invoke `cmake` as part of their own build (e.g. `wabt`).

## Supported hosts

macOS, Linux and Windows, on both X64 and Arm64.

## Extension

Applying the plugin registers a `cmake` tool exposing the below DSL:

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.cmake)
}

komple {
    cmake {
        version = "<cmake_version>"

        checksums {
            linuxAarch64 = "<linux_aarch64_checksum>"
            linuxX64 = "<linux_x64_checksum>"
            macos = "<macos_checksum>"
            windowsAarch64 = "<windows_aarch64_checksum>"
            windowsX64 = "<windows_x64_checksum>"
        }
    }
}
```

## Shell

The tool registers the path to the `cmake` executable, making it available in the shell.