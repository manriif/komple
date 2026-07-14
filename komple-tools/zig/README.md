# Module Komple Tool Zig

Downloads and installs the [Zig](https://ziglang.org/) toolchain and registers it as a C compiler
for Linux and Windows on C projects, via `zig cc`.

## Supported hosts

macOS, Linux and Windows are all supported for installing the tool itself, but only **Linux and
Windows** (Arm64 and X64) are registered as C-compilation targets — macOS is excluded from
Zig-as-C-compiler use.

## Extension

Applying the plugin registers a `zig` tool exposing the below DSL:

- `version` — Zig version to download.
- `publicKey` — Minisign public key used to verify the downloaded archive.
- `compilationParams.linuxVersionMin` / `windowsVersionMin` — minimum target OS version baked into
  the compiled target triple.

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.zig)
}

komple {
    zig {
        version = "<zig_version>"
        publicKey = "<minisign_public_key>"

        compilationParams {
            linuxVersionMin = "<min_linux_version>"
            windowsVersionMin = "<min_windows_version>"
        }
    }
}
```

## Download and install

The archive is fetched from a randomly chosen [community mirror](https://ziglang.org/download/community-mirrors.txt),
falling back to `ziglang.org` itself, and verified with its `.minisig` signature against
`publicKey`; a mirror that fails verification is retried on the next mirror in the list. The `zig` 
executable ends up directly at the root of the install directory, which is exposed on `PATH`.

## Compiling C sources

Compilation builds a target triple `<arch>-<os>[.<minVersion>]-gnu` and always invokes `zig cc
-target <triple>` (`zig ar` for static archiving).