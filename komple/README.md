# Module Komple

Core API of Komple: the Gradle-facing contracts a tool integration or a project type is built on —
`KompleToolConfigurator`/`KompleTool`, the `komple.exec` shell environment abstractions, the
`komple.task` checksum-tracked task types, and `CProject` for C sources. This module has no
opinion on which tools exist or how the plugin is wired into a build; that lives in
`komple-gradle-plugin`, and concrete tools live under `komple-tools`.

See the [project README](../README.md) for the full DSL and usage.