# Module Komple Tool Emscripten

Downloads and installs the [Emscripten SDK](https://emscripten.org/) (`emsdk`) and exposes its
environment (compilers, `PATH` entries, environment variables) to the shell.

## Supported hosts

macOS, Linux and Windows.

## Extension

Applying the plugin registers an `emscripten` tool exposing the below DSL:

- `version` — `emsdk` archive tag to download from `github.com/emscripten-core/emsdk`.
- `checksum` — SHA-256 checksum of the downloaded archive.
- `emscriptenVersion` — Emscripten toolchain version passed to `emsdk install`/`emsdk activate`
  (accepts a version string or `latest`); defaults to `version`.

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.emscripten)
}

komple {
    emscripten {
        version = "<version>"
        checksum = "<checksum>"
        emscriptenVersion = "latest"
    }
}
```

## Install and shell

Installation does not compile anything itself: it runs the SDK's own bootstrap, `./emsdk install
<emscriptenVersion>` followed by `./emsdk activate <emscriptenVersion>` (`emsdk.bat` on Windows).
`configureEnvironment` then sources `emsdk_env.sh` (macOS/Linux) or runs `emsdk_env.bat` (Windows)
so `emcc`/`em++` and the rest of the toolchain are available on `PATH` for other tasks.