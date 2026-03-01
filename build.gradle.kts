plugins {
    alias(libs.plugins.dokka) apply false // true
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.gradle.pluginPublish) apply false
}

allprojects {
    group = property("project.group").toString()
    version = rootProject.libs.versions.komple.get()
}