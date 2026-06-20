plugins {
    `kotlin-dsl`
    alias(libs.plugins.conventions.tool)
}

dependencies {
    implementation(libs.minisign4j)
}