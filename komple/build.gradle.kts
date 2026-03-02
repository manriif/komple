plugins {
    `kotlin-dsl`
    alias(libs.plugins.conventions.gradle)
}

dependencies {
    api(libs.kotlin.gradlePlugin)
    api(libs.undercouch.download)
}