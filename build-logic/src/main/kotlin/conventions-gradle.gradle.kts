plugins {
    `java-library`
    org.jetbrains.kotlin.jvm
    id("conventions-common")
}

dependencies {
    implementation(gradleApi())
}

kotlin {
    configureKotlin()

    sourceSets.all {
        languageSettings.optIn("komple.KompleInternalApi")
    }
}