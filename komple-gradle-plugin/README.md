# Module Komple Gradle Plugin

Gradle plugin (`io.github.manriif.komple`, `KomplePlugin`) implementing the abstractions defined
by the `komple` module: it turns registered tool configurators into installed tools, composes
their shell environments, orders their installation, and lets subprojects declare native projects
those tools can compile or generate code from.

Applied on the root project it creates a `komple { }` extension used to declare tools and
projects; applied on a subproject it creates a `komple { }` extension used to consume what the
root declared.

See the [project README](../README.md) for the full DSL and usage.