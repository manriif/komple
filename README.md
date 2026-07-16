[ksqlite]: https://github.com/manriif/ksqlite

[website]: https://manriif.github.io/komple

# Komple

[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-dokka-green)][website]
[![Maven Central](https://img.shields.io/maven-central/v/io.github.manriif.komple/komple-gradle-plugin?label=MavenCentral&logo=apache-maven)](https://central.sonatype.com/artifact/io.github.manriif.komple/komple-gradle-plugin)
[![Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.manriif.komple?label=Plugin%20Portal&logo=gradle)](https://plugins.gradle.org/plugin/io.github.manriif.komple)

Komple is a Gradle plugin that downloads, verifies, installs and exposes native development
tools, such as compilers, SDKs and CLI utilities, directly from your build. There's no need for
a system-wide package manager or a pre-provisioned CI image. Your build declares the tools it
needs, Komple pulls them into the project's own cache, and hands them back through a composable
shell environment and, for compilers, a native project abstraction any registered tool can
contribute to.

## Table of contents

- [Why](#why)
- [Concepts](#concepts)
- [Requirements](#requirements)
- [Getting started](#getting-started)
    - [Installing Komple](#installing-komple)
    - [Installing tools](#installing-tools)
    - [Configuring tools](#configuring-tools)
    - [Consuming tools](#consuming-tools)
- [Tool dependencies](#tool-dependencies)
- [Execution environments](#execution-environments)
- [Creating a (C) project](#creating-a-c-project)
- [Creating a custom tool](#creating-a-custom-tool)
- [Cache](#cache)
- [Real-world example](#real-world-example)
- [Modules](#modules)
- [Documentation](#documentation)
- [License](#license)
- [Contributing](#contributing)

## Why

Komple started as part of **[Ksqlite][ksqlite]**, a Kotlin Multiplatform SQLite bindings
library. As Ksqlite grew to need more toolchains (Android NDK, Xcode, Emscripten, ...) that had
nothing to do with SQLite itself, that part of the build was carved out into its own project,
called Komple.

## Concepts

- **Tool.** An external toolchain Komple can download, verify, extract and install on behalf of
  a build, from a full compiler to a plain CLI utility.
- **Shell.** The composable execution environment (`PATH` entries, environment variables) a tool
  exposes once installed. Other tools and custom tasks can depend on it to run the tool's
  executables.
- **Project.** A typed unit of native sources. Currently a C project (header, include
  directories, per-platform compiler/linker options, etc.) that installed tools can contribute a
  compiler or a binding generator to.

## Requirements

- Gradle 9.0.0 or newer (older versions probably work too, but that's untested)
- JDK 17 or above
- A few gigabytes of free disk space (SSD recommended). The exact amount depends on which tools
  you apply
- `make` available on Unix, required by tools that are built from source

> [!CAUTION]
> Windows support is shaky, mainly because I don't own a machine running it. Expect missing
> features or rough edges on the tools I couldn't personally test. Development happens on Apple
> Silicon. Linux should work too, but "should" is doing a lot of heavy lifting in that sentence.

> [!NOTE]
> Komple is built with Kotlin scripts (`build.gradle.kts`) in mind and hasn't been tested with
> Groovy, so your mileage may vary.

## Getting started

### Installing Komple

Komple is published as
a [version catalog](https://docs.gradle.org/current/userguide/version_catalogs.html#sec:importing-published-catalog)
that bundles a single `komple` version for every module and tool plugin:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    versionCatalogs {
        create("kompleLibs") {
            from("io.github.manriif.komple:komple-catalog:<version>")
        }
    }
}
```

That gives you the `komple` plugin alias, one `tool-<name>` plugin alias per built-in tool (e.g.
`tool-cmake`, `tool-androidNdk`, `tool-gnuSed`), and the `kompleGradlePlugin` library alias if
you need it in your `build-logic`.

### Installing tools

Apply the Komple plugin on the **root project**, plus one plugin per tool the build needs:

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.cmake)
    alias(kompleLibs.plugins.tool.zig)
}
```

Without the catalog, the same works with the plugins' raw IDs (`io.github.manriif.komple` and
`io.github.manriif.komple-tool-<name>`, e.g. `io.github.manriif.komple-tool-cmake`):

```kotlin
// root build.gradle.kts
plugins {
    id("io.github.manriif.komple") version "<version>"
    id("io.github.manriif.komple-tool-cmake") version "<version>"
    id("io.github.manriif.komple-tool-zig") version "<version>"
}
```

Each built-in tool plugin registers itself on apply. Komple installs tools lazily, only when
they're actually required, and every built-in tool is configuration-cache safe.

### Configuring tools

Every tool exposes its options under `komple { }` on the root project, named after the tool
(e.g. `cmake`, `androidNdk`, `zig`):

```kotlin
// root build.gradle.kts
komple {
    androidNdk {
        compilationParams {
            minSdk = "24"
        }
    }

    zig {
        version = "0.15.2"
    }
}
```

Every option of a built-in tool has a default (version, checksums, ...) sourced from the
project's own [`komple.properties`](komple.properties) and baked into each release, so a tool
can be applied and used without configuring anything. Set the corresponding property explicitly,
as shown above, to override a default.

> [!WARNING]
> Some tools let you change their version, but under normal circumstances there's no reason to.
> If you do, you must also supply the checksum(s) for the new artifact. Check the tool's source
> code to see where it fetches its files from.

### Consuming tools

The Komple plugin must be applied on any subproject that needs access to the tools applied on
the root. Every tool registered on the root is mirrored on the subproject's `komple.tools`
extension, so its install task and directory can be wired as task inputs:

```kotlin
// subproject build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
}

val myTask = tasks.register("myTask") {
    // Make this task depend on the cmake installation
    dependsOn(komple.tools.cmake.installTaskProvider)
}

// Accessing a tool's `installDirectory` creates an implicit dependency on its installation task
val androidNdkDirectory = komple.tools.androidNdk.installDirectory
```

## Tool dependencies

A tool can depend on another so that its install task waits for the dependency, and its shell
inherits the dependency's `PATH` and environment variables.

```kotlin
// root build.gradle.kts
komple {
    tools {
        wabt dependsOn cmake
    }
}
```

All dependencies are declared in the root build script. Some built-in tools carry dependencies
of their own that still need to be expressed there manually. Komple detects cycles between
tools and throws before your build has a chance to get confused about it.

## Execution environments

Beyond what a single tool contributes, `execEnvironments` composes several tools' shells into
one named environment a custom task can run commands against:

```kotlin
// root build.gradle.kts
import komple.exec.addEnvironments

komple {
    execEnvironments {
        register("wasm") {
            addEnvironments(tools.emscripten, tools.gnuSed, tools.wabt)
        }
    }
}
```

Root-level environments are mirrored on every subproject's `execEnvironments` extension, ready
to be turned into a command executor from a task. Registering an environment doesn't
automatically create task dependencies, though:

```kotlin
// subproject build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
}

val compileWasm = tasks.register("compileWasm") {
    val requiredTools = komple.tools.run {
        listOf(emscripten, gnuSed, wabt)
            .map(KompleTool::installTaskProvider)
            .toTypedArray()
    }

    // Ensure that emscripten, gnuSed and WABT are installed before the task runs
    dependsOn(*requiredTools)

    val wasmEnv = komple.execEnvironments.wasm
    val execOperations = serviceOf<ExecOperations>()

    // Implicit dependency on the sqlite tool; it could also be declared in requiredTools
    val sqliteDirectory = komple.tools.sqlite.installDirectory

    doLast {
        // Commands run through this executor see emscripten, gnuSed and WABT on the PATH; they
        // may run their own setup commands before yours are processed
        val executor = wasmEnv.createCommandExecutor(execOperations)
        val sqliteDir = sqliteDirectory.get().asFile
        val sqliteSrc = sqliteDir.resolve("sqlite3.c")

        executor.execute(
            command = Command("./configure") {
                then("make", "-j4", "64bit", "sqlite.c=${sqliteSrc.absolutePath}")
            },
            workingDirectory = sqliteDir
        )
    }
}
```

> [!WARNING]
> The example above is unsafe. Prefer implementing a proper `Task` so Gradle and Komple can
> track its inputs and outputs correctly.

## Creating a (C) project

Declare a C project once on the root, with a header, its include directories, and any
per-platform definitions, compiler/linker options and optimization level:

```kotlin
// root build.gradle.kts
import komple.platform.Platform
import komple.project.c.COptimization
import komple.project.c.CProject

komple {
    projects {
        register<CProject>("sqlite") {
            packageName = "sqlite" // used by code generator tools
            libraryName = "sqlite" // produces libsqlite.<a/so/dylib>, sqlite.dll, etc.
            headerFile = file("sqlite/sqlite.h")
            headerFilters.from(headerFile)
            sourceFiles.from(file("sqlite/sqlite3.c"))
            includeDirectories.from(file("sqlite"))

            // Affects all platforms
            definitions = mapOf("SQLITE_OMIT_AUTOINIT" to "1")
            optimization = COptimization.Level2
            compilerOptions.addAll("-g0", "-DNDEBUG")

            // Per-platform configuration
            optimization(Platform.androidArm64, COptimization.Size) // override the default

            Platform.run {
                listOf(linuxArm64, linuxX64, macosArm64, macosX64).forEach { platform ->
                    definition(platform) {
                        put("IS_UNIX", "1")
                    }

                    linkerOptions(platform) {
                        addAll("-lpthread")
                    }
                }
            }
        }
    }
}
```

The project is then handed to every registered tool, which can contribute to it however it
likes. The `jextract` tool, for instance, declares a DSL for generating bindings from the
project's sources.

On a subproject, ask for a library on a given platform. Whichever registered tool supports that
platform compiles it (an error is thrown if none does):

```kotlin
// subproject build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
}

val libraries = Platform.run {
    listOf(linuxArm64, macosArm64, mingwArm64).map { platform ->
        // Declare a shared library for platform
        // The library is generated only when required
        komple.projects.sqlite.createLibrary(CLibraryType.Shared, platform)
    }
}

kotlin {
    jvm {
        compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME).configure {
            tasks.named<ProcessResources>(processResourcesTaskName).configure {
                libraries.forEach { library ->
                    // library.libraryFile creates an implicit dependency on the library
                    // compilation task
                    from(library.libraryFile) {
                        into(library.compilation.platform.map { platform ->
                            "native/${platform.name}"
                        })
                    }
                }
            }
        }
    }
}
```

For a Kotlin/Native target, the overload taking a `KotlinNativeTarget` additionally generates
and wires a `.def` file into the target's `cinterops`, so the compiled library is usable from
Kotlin/Native without a handwritten one:

```kotlin
// subproject build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
}

kotlin {
    val linux = linuxX64()

    // Declare a static library for linux
    // The library is generated only when required, but cinterop triggers it immediately
    komple.projects.sqlite.createLibrary(CLibraryType.Static, linux) {
        generateDefFileTaskProvider.configure {
            // Ensure the sqlite tool is installed and its sources are available
            dependsOn(komple.tools.sqlite.installTaskProvider)
        }

        // Add extra instructions to the .def file generator task
        excludedFunctions.add("sqlite3_win32_set_directory8")
        noStringConversion.addAll("sqlite3_prepare_v2", "sqlite3_prepare_v3")

        extraOpts("-Xccall-mode", "direct")
    }
}
```

For Kotlin/Native, the static library currently ends up bundled inside the `.klib`.

> [!WARNING]
> Kotlin caches the generated static library and doesn't seem to invalidate it. So even when
> Komple detects a change in the C project (a new preprocessor definition, etc.) and Gradle
> invalidates the compilation task, you may still need to clear the subproject's build cache
> (`build/classes` appears to be the culprit).

## Creating a custom tool

A custom tool is a class implementing
[`KompleToolConfigurator`](komple/src/main/kotlin/komple/tool/configurator/KompleToolConfigurator.kt)
([`DefaultKompleToolConfigurator`](komple/src/main/kotlin/komple/tool/configurator/DefaultKompleToolConfigurator.kt)
and [`VersionedKompleToolConfigurator`](komple/src/main/kotlin/komple/tool/configurator/VersionedKompleToolConfigurator.kt)
are convenience base classes). It's asked, in order, to check host support, configure its own
DSL extension, register its download/integrity/extract/install tasks, configure the shell it
contributes, and optionally contribute a compiler to a project type. See the built-in tools for 
real examples.

Komple also ships several optional built-in tasks for common operations, such as downloading
from a URL, SHA-x integrity checks, unarchiving (`.zip`, `.tar.gz`, `.tar.xz`), `.dmg`
extraction, and extraction/installation from a command. Use them as-is, or extend them for more
advanced cases.

A custom tool is registered from a
[`KompleToolPlugin`](komple/src/main/kotlin/komple/tool/KompleToolPlugin.kt) and applied on the
root project like any other tool plugin:

```kotlin
// build-logic/src/main/kotlin/MyToolPlugin.kt
import komple.KompleRootExtension
import komple.registerTool
import komple.tool.KompleToolPlugin

class MyToolPlugin : KompleToolPlugin() {
    override fun configure(project: Project, komple: KompleRootExtension) {
        komple.registerTool<MyToolConfigurator>("My Tool")
    }
}
```

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.appleXcode)

    // Custom plugin
    alias(libs.plugins.myToolPlugin)
}
```

## Cache

Downloaded archives, extracted files and installed tools are cached under
`<rootDir>/.komple/{downloads,extracts,installs}`.

> [!TIP]
> Add `<rootDir>/.komple` to your VCS ignore list. Nobody needs several gigabytes of toolchains
> sitting in their git history.

Komple also provides a tracking mechanism that works independently of Gradle's own snapshotting 
system, and can block the execution of a task that Gradle marked as OUT-OF-DATE. Per-task checksums
live under `<rootDir>/.komple/checksums` and track a task's inputs and output files, though only a 
subset of input types is currently supported.

Both the Komple tracker and Gradle's build cache can be enabled for the same task. Gradle's
cache can skip the task entirely, while Komple's tracker spends a few milliseconds comparing
checksums. For large files, though, those milliseconds pay for themselves many times over,
saving seconds to minutes depending on your hardware and the size of the task's inputs and
outputs.

A [TaskStateTracker](komple/src/main/kotlin/komple/task/TaskStateTracker.kt) is always passed to the 
download, extract and install task of a tool when using one of the built-in tasks. For other tasks:

```kotlin
import komple.gradle.task.track
import komple.task.TaskStateTracker
import komple.task.enableTracking
import komple.task.hasChanged

@DisableCachingByDefault
abstract class MyDownloadTask: DefaultTask() {

    // TaskStateTracker must not be declared as task input
    @get:Internal
    abstract val tracker: Property<TaskStateTracker>

    @TaskAction
    fun theAction() {
        if (tracker.get().hasChanged()) {
            downloadTheContent()
        }
    }
}

val myDownloadTask = tasks.register<MyDownloadTask>("myDownloadTask") {
    track { tracker ->
        this.tracker = tracker
        // Tracking is disabled by default
        tracker.enableTracking()
    }

    // No action should be added to the task starting from here
}
```

## Real-world example

**[Ksqlite][ksqlite]**, the project Komple was extracted from, is a complete real-world
consumer:

- its root `build.gradle.kts` registers and configures tools and a C project
- its `compile-logic` included build implements custom tools (including SQLite itself, built
  from source)
- its `ksqlite-foreign` and `ksqlite-wasm-resources` subprojects generate native static and
  shared libraries, consume jextract-generated bindings, and use a custom `wasm` execution
  environment

> [!NOTE]
> The current built-in tools are exactly the ones needed to compile SQLite for every Kotlin
> target **Ksqlite** supports, nothing more, nothing less.

## Modules

| Module                                                 | Plugin ID                                   | Description                                                                |
|--------------------------------------------------------|---------------------------------------------|----------------------------------------------------------------------------|
| [`komple`](komple)                                     | -                                           | Core API.                                                                  |
| [`komple-gradle-plugin`](komple-gradle-plugin)         | `io.github.manriif.komple`                  | The Gradle plugin gluing tools, shells and projects together.              |
| [`komple-catalog`](komple-catalog)                     | -                                           | Version catalog publishing every Komple artifact and tool.                 |
| [`komple-tools/android-ndk`](komple-tools/android-ndk) | `io.github.manriif.komple-tool-android-ndk` | [Android NDK](https://developer.android.com/ndk) — C compiler for Android. |
| [`komple-tools/apple-xcode`](komple-tools/apple-xcode) | `io.github.manriif.komple-tool-apple-xcode` | Local Xcode toolchain — C compiler for macOS, iOS, tvOS, watchOS.          |
| [`komple-tools/cmake`](komple-tools/cmake)             | `io.github.manriif.komple-tool-cmake`       | [CMake](https://cmake.org/).                                               |
| [`komple-tools/emscripten`](komple-tools/emscripten)   | `io.github.manriif.komple-tool-emscripten`  | [Emscripten SDK](https://emscripten.org/).                                 |
| [`komple-tools/gnu-sed`](komple-tools/gnu-sed)         | `io.github.manriif.komple-tool-gnu-sed`     | [GNU sed](https://www.gnu.org/software/sed/).                              |
| [`komple-tools/jextract`](komple-tools/jextract)       | `io.github.manriif.komple-tool-jextract`    | [jextract](https://jdk.java.net/jextract/) — Java FFM binding generator.   |
| [`komple-tools/wabt`](komple-tools/wabt)               | `io.github.manriif.komple-tool-wabt`        | [WABT](https://github.com/WebAssembly/wabt) — WebAssembly Binary Toolkit.  |
| [`komple-tools/zig`](komple-tools/zig)                 | `io.github.manriif.komple-tool-zig`         | [Zig](https://ziglang.org/) — C compiler via `zig cc`.                     |

## Documentation

API documentation is generated with [Dokka](https://kotlinlang.org/docs/dokka-introduction.html)
and published [here][website].

## License

Komple is licensed under the [MIT License](LICENSE).

## Contributing

Contributions are welcome, whether it's a bug fix or a brand new tool plugin. Feel free to open
an issue or a pull request.