# Module Komple Tool Jextract

Downloads and installs [jextract](https://jdk.java.net/jextract/) and generates Java FFM (Panama)
bindings from the header of a C project.

## Supported hosts

macOS and Linux (both X64 and Arm64); Windows X64 only (no Windows Arm64).

## Extension

Applying the plugin registers a `jextract` tool exposing the below DSL:

- `version` — jextract build version to download.
- `jdkVersion` (`JavaVersion`) — JDK version jextract is built against; must be one jextract
  publishes prebuilt binaries for.
- `checksums` — per-OS/arch SHA-256 checksums (`linuxAarch64`, `linuxX64`, `macosAarch64`,
  `macosX64`, `windowsX64`).

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.jextract)
}

komple {
    jextract {
        version = "<jextract_version>"
        jdkVersion = JavaVersion.VERSION_22

        checksums {
            linuxAarch64 = "<linux_aarch64_checksum>"
            linuxX64 = "<linux_x64_checksum>"
            macosAarch64 = "<macos_aarch64_checksum>"
            macosX64 = "<macos_x64_checksum>"
            windowsX64 = "<windows_x64_checksum>"
        }
    }
}
```

The archive is downloaded from `download.java.net/java/early_access/jextract`, and its `bin`
directory is exposed on `PATH` — the `jextract` binary itself is usable from any task that depends
on the tool's shell, in addition to the binding generation below.

## Generating bindings

Every C project gets a `jextract` extension exposing a `bindingGenerators` container. Each named
generator registers a task that invokes `jextract` against the project's `headerFile` (with its
`includeDirectories` and `definitions`), writing generated Java sources to its own output
directory:

```kotlin
// subproject build.gradle.kts

// Supposing a project "myProject" was declared in the root build.gradle.kts:
val javaBindings = komple.projects.myProject.jextract.bindingingGenerators.register("javaSources") {
    options {
        headerClassName = "sqlite3"
        includeConstants.addAll("SQLITE_TRANSIENT")
        includeStructs.addAll("sqlite3_vtab_cursor", "sqlite3_vfs")
    }
}

kotlin {
    sourceSets.jvmMain {
        generatedKotlin.srcDir(javaBindings.flatMap { it.generateDirectory })
    }
}
```

Note that the generated Java sources needs to be compiled.