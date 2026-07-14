# Module Komple Tool GNU sed

Downloads, builds from source and installs [GNU sed](https://www.gnu.org/software/sed/) and
exposes it on the shell `PATH`. It is a plain shell-environment tool and does not contribute a
compiler to a C project.

## Supported hosts

macOS and Linux — Windows is not supported.

## Extension

Applying the plugin registers a `gnuSed` tool exposing the shared `version`/`checksum`
(SHA-256) extension, with no GNU sed-specific properties:

```kotlin
// root build.gradle.kts
plugins {
    alias(kompleLibs.plugins.komple)
    alias(kompleLibs.plugins.tool.gnuSed)
}

komple {
    gnuSed {
        version = "<gnu_sed_version>"
        checksum = "<checksum>"
    }
}
```

## Install

The source archive is built with `./configure`/`make`/`make install`. `sed` ends up on `PATH`
from `build/bin` in the install directory.