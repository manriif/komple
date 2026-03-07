rootProject.name = "komple-project"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

include(":komple")
include(":komple-catalog")
include(":komple-gradle-plugin")

fun includeTool(name: String) {
    include(":komple-tools:$name")
}

includeTool("android-ndk")
includeTool("jextract")