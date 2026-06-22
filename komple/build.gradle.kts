plugins {
    `kotlin-dsl`
    alias(libs.plugins.conventions.gradle)
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
}