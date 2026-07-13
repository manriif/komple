# Module Komple Tool WABT

Downloads, builds and installs the [WebAssembly Binary Toolkit](https://github.com/WebAssembly/wabt)
(WABT — `wat2wasm`, `wasm2wat`, and friends) and exposes it on the shell `PATH`. It is a plain
shell-environment tool and does not contribute a compiler to a C project.

## Supported hosts

macOS and Linux — Windows is not supported.

## Dependencies

WABT requires the cmake tool to be installed and set as a dependency.

## Extension

Applying the plugin registers a `wabt` tool exposing the below DSL:

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.wabt)
}

komple {
    wabt {
        version = "<wabt_version>"
        checksum = "<checksum>"
    }
}
```

## Shell

The resulting `bin` directory is exposed on `PATH`.